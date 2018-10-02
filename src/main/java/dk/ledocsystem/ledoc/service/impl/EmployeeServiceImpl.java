package dk.ledocsystem.ledoc.service.impl;

import com.google.common.collect.ImmutableMap;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.Expressions;
import dk.ledocsystem.ledoc.config.security.JwtTokenService;
import dk.ledocsystem.ledoc.config.security.UserAuthorities;
import dk.ledocsystem.ledoc.dto.employee.EmployeeCreateDTO;
import dk.ledocsystem.ledoc.dto.employee.EmployeeDetailsEditDTO;
import dk.ledocsystem.ledoc.dto.employee.EmployeeEditDTO;
import dk.ledocsystem.ledoc.dto.projections.EmployeeNames;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.Location;
import dk.ledocsystem.ledoc.model.email_notifications.EmailNotification;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.model.employee.EmployeeDetails;
import dk.ledocsystem.ledoc.model.employee.QEmployee;
import dk.ledocsystem.ledoc.repository.EmailNotificationRepository;
import dk.ledocsystem.ledoc.repository.EmployeeRepository;
import dk.ledocsystem.ledoc.service.EmployeeService;
import dk.ledocsystem.ledoc.service.LocationService;
import dk.ledocsystem.ledoc.util.BeanCopyUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
class EmployeeServiceImpl implements EmployeeService {

    private static final Function<Long, Predicate> CUSTOMER_EQUALS_TO =
            customerId -> ExpressionUtils.eqConst(QEmployee.employee.customer.id, customerId);

    private final EmployeeRepository employeeRepository;
    private final LocationService locationService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService tokenService;
    private final EmailNotificationRepository emailNotificationRepository;

    @Transactional
    @Override
    public Employee createEmployee(@NonNull EmployeeCreateDTO employeeCreateDTO, @NonNull Customer customer) {
        Employee employee = new Employee();
        BeanCopyUtils.copyProperties(employeeCreateDTO, employee, false);
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employee.setCustomer(customer);

        Employee responsible = resolveResponsible(employeeCreateDTO.getResponsibleId());
        employee.setResponsible(responsible);

        Set<Location> locations = resolveLocations(employeeCreateDTO.getLocationIds());
        employee.setLocations(locations);

        if (shouldEmployeeBeReviewed(employeeCreateDTO)) {
            Long skillResponsibleId = employeeCreateDTO.getDetails().getSkillResponsibleId();
            employee.getDetails().setResponsibleOfSkills(resolveResponsibleOfSkills(skillResponsibleId));
        }

        employee = employeeRepository.save(employee);

        addAuthorities(employee, employeeCreateDTO);
        sendMessages(employeeCreateDTO, responsible);
        return employee;
    }

    private boolean shouldEmployeeBeReviewed(EmployeeCreateDTO employeeCreateDTO) {
        return employeeCreateDTO.getDetails() != null && employeeCreateDTO.getDetails().isSkillAssessed();
    }

    private void addAuthorities(Employee employee, EmployeeCreateDTO employeeCreateDTO) {
        String roleString = ObjectUtils.defaultIfNull(employeeCreateDTO.getRole(), "user");
        addAuthorities(employee, UserAuthorities.fromString(roleString));

        if (employeeCreateDTO.isCanCreatePersonalLocation()) {
            addAuthorities(employee, UserAuthorities.CAN_CREATE_PERSONAL_LOCATION);
        }
    }

    @Transactional
    @Override
    public Employee createPointOfContact(@NonNull EmployeeCreateDTO employeeCreateDTO, Customer customer) {
        Employee poc = createEmployee(employeeCreateDTO, customer);
        addAuthorities(poc, UserAuthorities.SUPER_ADMIN);
        return poc;
    }

    @Transactional
    @Override
    public Employee updateEmployee(@NonNull Long employeeId, @NonNull EmployeeEditDTO employeeEditDTO) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("employee.id.not.found", employeeId.toString()));
        BeanCopyUtils.copyProperties(employeeEditDTO, employee, false);

        Long responsibleId = employeeEditDTO.getResponsibleId();
        if (responsibleId != null) {
            Employee responsible = resolveResponsible(responsibleId);
            employee.setResponsible(responsible);
            sendNotificationToResponsible(responsible);
        }

        Set<Long> locationIds = employeeEditDTO.getLocationIds();
        if (locationIds != null) {
            employee.setLocations(resolveLocations(locationIds));
        }

        if (employeeDetailsChanged(employeeEditDTO)) {
            updateReviewDetails(employeeEditDTO.getDetails(), employee.getDetails());
        }

        if (employeeEditDTO.getRole() != null) {
            changeRole(employee, UserAuthorities.fromString(employeeEditDTO.getRole()));
        }

        return employeeRepository.save(employee);
    }

    private boolean employeeDetailsChanged(EmployeeEditDTO employeeEditDTO) {
        return employeeEditDTO.getDetails() != null;
    }

    private void updateReviewDetails(EmployeeDetailsEditDTO editDTO, EmployeeDetails employeeDetails) {
        if (BooleanUtils.isFalse(editDTO.getSkillAssessed())) {
            employeeDetails.eraseReviewDetails();
        } else {
            Long skillResponsibleId = editDTO.getSkillResponsibleId();
            if (skillResponsibleId != null) {
                employeeDetails.setResponsibleOfSkills(resolveResponsibleOfSkills(skillResponsibleId));
            }
        }
    }

    private void changeRole(Employee employee, UserAuthorities role) {
        Set<UserAuthorities> authorities = employee.getAuthorities();
        if (role == UserAuthorities.USER) {
            authorities.remove(UserAuthorities.ADMIN);
            authorities.add(UserAuthorities.USER);
        } else {
            authorities.remove(UserAuthorities.USER);
            authorities.add(UserAuthorities.ADMIN);
        }
        tokenService.updateTokens(employee.getId(), employee.getAuthorities());
    }

    @Override
    public void changePassword(@NonNull String username, @NonNull String newPassword) {
        employeeRepository.changePassword(username, newPassword);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public void grantAuthorities(@NonNull Long employeeId, @NonNull UserAuthorities authorities) {
        Employee employee = getById(employeeId)
                .orElseThrow(() -> new NotFoundException("employee.id.not.found", employeeId.toString()));
        employee.getAuthorities().add(authorities);
        tokenService.updateTokens(employeeId, employee.getAuthorities());
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public void revokeAuthorities(@NonNull Long employeeId, @NonNull UserAuthorities authorities) {
        Employee employee = getById(employeeId)
                .orElseThrow(() -> new NotFoundException("employee.id.not.found", employeeId.toString()));
        employee.getAuthorities().remove(authorities);
        tokenService.updateTokens(employeeId, employee.getAuthorities());
    }

    @Override
    public boolean existsByUsername(@NonNull String username) {
        return employeeRepository.existsByUsername(username);
    }

    @Override
    public List<Employee> getAllForReview() {
        return employeeRepository.findAllForReview();
    }

    @Override
    public List<EmployeeNames> getAllByRole(@NonNull UserAuthorities authorities) {
        return employeeRepository.findAllByAuthoritiesContains(authorities);
    }

    @Override
    public Page<Employee> getNewEmployees(@NonNull Long userId, @NonNull Pageable pageable) {
        return getNewEmployees(userId, pageable, null);
    }

    @Override
    public Page<Employee> getNewEmployees(@NonNull Long userId, @NonNull Pageable pageable, Predicate predicate) {
        Employee employee = getById(userId)
                .orElseThrow(() -> new NotFoundException("employee.id.not.found", userId.toString()));
        Long customerId = employee.getCustomer().getId();

        Predicate newEmployeesPredicate = ExpressionUtils.allOf(
                predicate,
                QEmployee.employee.archived.eq(Boolean.FALSE),
                ExpressionUtils.neConst(QEmployee.employee.id, userId),
                ExpressionUtils.notIn(Expressions.constant(employee), QEmployee.employee.visitedBy));
        return getAllByCustomer(customerId, newEmployeesPredicate, pageable);
    }

    @Override
    public Employee getCurrentUserReference() {
        Long currentUserId = getCurrentUser().getUserId();
        return employeeRepository.getOne(currentUserId);
    }

    private void addAuthorities(Employee employee, UserAuthorities userAuthorities) {
        employeeRepository.addAuthorities(employee.getId(), userAuthorities);
    }

    private Employee resolveResponsibleOfSkills(Long responsibleId) {
        return getById(responsibleId)
                .orElseThrow(() -> new NotFoundException("employee.responsible.of.skills.not.found", responsibleId.toString()));
    }

    private Employee resolveResponsible(Long responsibleId) {
        return (responsibleId == null) ? null :
                getById(responsibleId)
                        .orElseThrow(() -> new NotFoundException("employee.responsible.not.found", responsibleId.toString()));
    }

    private Set<Location> resolveLocations(Set<Long> locationIds) {
        return new HashSet<>(locationService.getAllById(locationIds));
    }

    private void sendMessages(EmployeeCreateDTO employee, Employee responsible) {
        if (employee.isWelcomeMessage()) {
            sendWelcomeMessage(employee);
        }

        if (responsible != null) {
            sendNotificationToResponsible(responsible);
        }
    }

    private void sendWelcomeMessage(EmployeeCreateDTO employee) {
        Map<String, Object> model = ImmutableMap.<String, Object>builder()
                .put("username", employee.getUsername())
                .put("password", employee.getPassword())
                .build();
        EmailNotification welcomeMessage = new EmailNotification(employee.getUsername(), "welcome", model);
        emailNotificationRepository.save(welcomeMessage);
    }

    private void sendNotificationToResponsible(Employee responsible) {
        EmailNotification notification =
                new EmailNotification(responsible.getUsername(), "employee_created");
        emailNotificationRepository.save(notification);
    }

    //region GET/DELETE standard API

    @Override
    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }

    @Override
    public Page<Employee> getAll(@NonNull Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }

    @Override
    public List<Employee> getAll(@NonNull Predicate predicate) {
        return IterableUtils.toList(employeeRepository.findAll(predicate));
    }

    @Override
    public Page<Employee> getAll(@NonNull Predicate predicate, @NonNull Pageable pageable) {
        return employeeRepository.findAll(predicate, pageable);
    }

    @Override
    public List<Employee> getAllByCustomer(@NonNull Long customerId) {
        return getAllByCustomer(customerId, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<Employee> getAllByCustomer(@NonNull Long customerId, @NonNull Pageable pageable) {
        return getAllByCustomer(customerId, null, pageable);
    }

    @Override
    public List<Employee> getAllByCustomer(@NonNull Long customerId, Predicate predicate) {
        return getAllByCustomer(customerId, predicate, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<Employee> getAllByCustomer(@NonNull Long customerId, Predicate predicate, @NonNull Pageable pageable) {
        Predicate combinePredicate = ExpressionUtils.and(predicate, CUSTOMER_EQUALS_TO.apply(customerId));
        return employeeRepository.findAll(combinePredicate, pageable);
    }

    @Override
    public Optional<Employee> getById(@NonNull Long id) {
        return employeeRepository.findById(id);
    }

    @Override
    public List<Employee> getAllById(@NonNull Iterable<Long> ids) {
        return employeeRepository.findAllById(ids);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void deleteById(@NonNull Long id) {
        tokenService.invalidateByUserId(id);
        employeeRepository.deleteById(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void deleteByIds(@NonNull Iterable<Long> employeeIds) {
        tokenService.invalidateByUserIds(employeeIds);
        employeeRepository.deleteByIdIn(employeeIds);
    }

    //endregion
}
