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
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.email_notifications.EmailNotification;
import dk.ledocsystem.ledoc.model.Avatar;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.model.employee.EmployeeDetails;
import dk.ledocsystem.ledoc.model.employee.QEmployee;
import dk.ledocsystem.ledoc.repository.EmailNotificationRepository;
import dk.ledocsystem.ledoc.repository.EmployeeRepository;
import dk.ledocsystem.ledoc.dto.projections.EmployeeNames;
import dk.ledocsystem.ledoc.service.CustomerService;
import dk.ledocsystem.ledoc.service.EmployeeService;
import dk.ledocsystem.ledoc.util.BeanCopyUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
class EmployeeServiceImpl implements EmployeeService {

    private static final Function<Long, Predicate> CUSTOMER_EQUALS_TO =
            (customerId) -> ExpressionUtils.eqConst(QEmployee.employee.customer.id, customerId);

    private final EmployeeRepository employeeRepository;
    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService tokenService;
    private final EmailNotificationRepository emailNotificationRepository;

    @Override
    public List<Employee> getAll() {
        return getAll(Pageable.unpaged()).getContent();
    }

    @Override
    public Page<Employee> getAll(@NonNull Pageable pageable) {
        return getAll(null, pageable);
    }

    @Override
    public List<Employee> getAll(@NonNull Predicate predicate) {
        return getAll(predicate, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<Employee> getAll(Predicate predicate, @NonNull Pageable pageable) {
        Long currentCustomerId = customerService.getCurrentCustomerReference().getId();
        Predicate combinePredicate = ExpressionUtils.and(predicate, CUSTOMER_EQUALS_TO.apply(currentCustomerId));
        return employeeRepository.findAll(combinePredicate, pageable);
    }

    @Override
    public Optional<Employee> getById(@NonNull Long id) {
        return employeeRepository.findById(id);
    }

    @Override
    public Employee getCurrentUserReference() {
        Long currentUserId = getCurrentUser().getUserId();
        return employeeRepository.getOne(currentUserId);
    }

    @Transactional
    @Override
    public Employee createEmployee(@NonNull EmployeeCreateDTO employeeCreateDTO) {
        return createEmployee(employeeCreateDTO, customerService.getCurrentCustomerReference());
    }

    @Transactional
    @Override
    public Employee createPointOfContact(@NonNull EmployeeCreateDTO employeeCreateDTO) {
        Employee poc = createEmployee(employeeCreateDTO, customerService.getCurrentCustomerReference());
        addAuthorities(poc.getId(), UserAuthorities.SUPER_ADMIN);
        return poc;
    }

    @Transactional
    @Override
    public Employee createEmployee(@NonNull EmployeeCreateDTO employeeCreateDTO, @NonNull Customer customer) {
        Employee employee = new Employee();
        BeanCopyUtils.copyProperties(employeeCreateDTO, employee, false);
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employee.setCustomer(customer);

        Employee responsible = resolveResponsible(employeeCreateDTO.getResponsibleId());
        employee.setResponsible(responsible);

        if (shouldEmployeeBeReviewed(employeeCreateDTO)) {
            Long skillResponsibleId = employeeCreateDTO.getDetails().getSkillResponsibleId();
            employee.getDetails().setResponsibleOfSkills(resolveResponsibleOfSkills(skillResponsibleId));
        }

        setAvatar(employeeCreateDTO.getAvatar(), employee);

        employee = employeeRepository.save(employee);

        sendMessages(employeeCreateDTO, responsible);
        if (employeeCreateDTO.isCanCreatePersonalLocation()) {
            addAuthorities(employee.getId(), UserAuthorities.CAN_CREATE_PERSONAL_LOCATION);
        }

        return employee;
    }

    private boolean shouldEmployeeBeReviewed(EmployeeCreateDTO employeeCreateDTO) {
        return employeeCreateDTO.getDetails() != null && employeeCreateDTO.getDetails().getSkillAssessed();
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

        if (employeeDetailsChanged(employeeEditDTO)) {
            updateReviewDetails(employeeEditDTO.getDetails(), employee.getDetails());
        }
        setAvatar(employeeEditDTO.getAvatar(), employee);
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

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteById(@NonNull Long id) {
        tokenService.invalidateByUserId(id);
        employeeRepository.deleteById(id);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void deleteByIds(@NonNull Collection<Long> employeeIds) {
        tokenService.invalidateByUserIds(employeeIds);
        employeeRepository.deleteByIdIn(employeeIds);
    }

    @Override
    public void changePassword(@NonNull String username, @NonNull String newPassword) {
        employeeRepository.changePassword(username, newPassword);
    }

    @Transactional
    @Override
    public void addAuthorities(@NonNull Long employeeId, @NonNull UserAuthorities authorities) {
        employeeRepository.addAuthorities(employeeId, authorities);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Override
    public void updateAuthorities(Long employeeId, UserAuthorities authorities) {
        tokenService.updateTokens(employeeId, authorities);
    }

    @Override
    public boolean existsByUsername(@NonNull String username) {
        return employeeRepository.existsByUsername(username);
    }

    @Override
    public List<Employee> findAllById(@NonNull Collection<Long> ids) {
        return employeeRepository.findAllById(ids);
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
    public Page<Employee> getNewEmployees(@NonNull Pageable pageable) {
        return getNewEmployees(pageable, null);
    }

    @Override
    public Page<Employee> getNewEmployees(@NonNull Pageable pageable, Predicate predicate) {
        Employee currentUser = getCurrentUserReference();

        Predicate newEmployeesPredicate = ExpressionUtils.allOf(
                predicate,
                ExpressionUtils.neConst(QEmployee.employee.id, currentUser.getId()),
                ExpressionUtils.notIn(Expressions.constant(currentUser), QEmployee.employee.visitedBy));
        return getAll(newEmployeesPredicate, pageable);
    }

    @Override
    public long countNewEmployees(@NonNull Long customerId, @NonNull Long employeeId) {
        long allEmployees = employeeRepository.countByCustomerIdAndArchivedFalse(customerId);
        long visitedEmployees = employeeRepository.countVisited(employeeId);
        return allEmployees - visitedEmployees - 1;
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

    private void setAvatar(String avatar, Employee employee) {
        if(avatar != null) {
            Avatar employeeAvatar = new Avatar();
            employeeAvatar.setAvatar(avatar);
            employee.setAvatarEmployee(employeeAvatar);
        }
    }

    public void sendMessages(EmployeeCreateDTO employee, Employee responsible) {
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
}
