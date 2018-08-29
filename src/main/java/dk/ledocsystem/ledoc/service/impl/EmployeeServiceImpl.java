package dk.ledocsystem.ledoc.service.impl;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.config.security.UserAuthorities;
import dk.ledocsystem.ledoc.dto.employee.EmployeeCreateDTO;
import dk.ledocsystem.ledoc.dto.employee.EmployeeEditDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.model.employee.QEmployee;
import dk.ledocsystem.ledoc.repository.EmployeeRepository;
import dk.ledocsystem.ledoc.dto.projections.EmployeeNames;
import dk.ledocsystem.ledoc.service.CustomerService;
import dk.ledocsystem.ledoc.service.EmployeeService;
import dk.ledocsystem.ledoc.service.SimpleMailService;
import dk.ledocsystem.ledoc.util.BeanCopyUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
class EmployeeServiceImpl implements EmployeeService {

    private static final Function<Long, Predicate> CUSTOMER_EQUALS_TO =
            (customerId) -> ExpressionUtils.eqConst(QEmployee.employee.customer.id, customerId);
    private static final Predicate ARCHIVED_FALSE = ExpressionUtils.eqConst(QEmployee.employee.archived, false);

    private final EmployeeRepository employeeRepository;
    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;
    private final SimpleMailService mailService;

    @Value("${spring.mail.username}")
    private String fromEmailAddress;

    @Override
    public List<Employee> getAll() {
        return getAll(Pageable.unpaged()).getContent();
    }

    @Override
    public Page<Employee> getAll(@NonNull Pageable pageable) {
        return getAll(ARCHIVED_FALSE, pageable);
    }

    @Override
    public List<Employee> getAll(@NonNull Predicate predicate) {
        return getAll(predicate, Pageable.unpaged()).getContent();
    }

    @Override
    public Page<Employee> getAll(@NonNull Predicate predicate, @NonNull Pageable pageable) {
        Long currentCustomerId = customerService.getCurrentCustomerReference().getId();
        Predicate combinePredicate = ExpressionUtils.and(predicate, CUSTOMER_EQUALS_TO.apply(currentCustomerId));
        return employeeRepository.findAll(combinePredicate, pageable);
    }

    @Override
    public Optional<Employee> getById(@NonNull Long id) {
        return employeeRepository.findById(id);
    }

    @Transactional
    @Override
    public Employee createEmployee(@NonNull EmployeeCreateDTO employeeCreateDTO) {
        return createEmployee(employeeCreateDTO, customerService.getCurrentCustomerReference());
    }

    @Transactional
    @Override
    public Employee createEmployee(@NonNull EmployeeCreateDTO employeeCreateDTO, @NonNull Customer customer) {
        Employee employee = new Employee();
        BeanCopyUtils.copyProperties(employeeCreateDTO, employee);
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employee.setCustomer(customer);
        employee.setResponsible(resolveResponsible(employeeCreateDTO.getResponsibleId()));

        Long skillResponsibleId = employeeCreateDTO.getDetails().getSkillResponsibleId();
        employee.getDetails().setResponsibleOfSkills(resolveResponsibleOfSkills(skillResponsibleId));

        employee = employeeRepository.save(employee);

        buildAndSendMessage(employeeCreateDTO);
        if (employeeCreateDTO.isCanCreatePersonalLocation()) {
            addAuthorities(employee.getId(), UserAuthorities.CAN_CREATE_PERSONAL_LOCATION);
        }

        return employee;
    }

    @Transactional
    @Override
    public Employee updateEmployee(@NonNull Long employeeId, @NonNull EmployeeEditDTO employeeEditDTO) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("employee.id.not.found", employeeId.toString()));
        BeanCopyUtils.copyProperties(employeeEditDTO, employee, false);

        Long skillResponsibleId = employeeEditDTO.getDetails().getSkillResponsibleId();
        if (skillResponsibleId != null) {
            employee.getDetails().setResponsibleOfSkills(resolveResponsibleOfSkills(skillResponsibleId));
        }

        return employeeRepository.save(employee);
    }

    @Override
    public void deleteById(@NonNull Long id) {
        employeeRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void deleteByIds(@NonNull Collection<Long> employeeIds) {
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

    @Override
    public Optional<Employee> getByUsername(@NonNull String username) {
        return employeeRepository.findByUsername(username);
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
    public List<EmployeeNames> getAllByRole(@NonNull UserAuthorities authorities) {
        return employeeRepository.findAllByAuthoritiesContains(authorities);
    }

    @Override
    public Page<Employee> getNewEmployees(@NonNull Long customerId, @NonNull Long employeeId, @NonNull Pageable pageable) {
        return employeeRepository.getNotVisited(customerId, employeeId, pageable);
    }

    @Override
    public long countNewEmployees(@NonNull Long customerId, @NonNull Long employeeId) {
        long allEmployees = employeeRepository.countByCustomerIdAndArchivedFalse(customerId);
        long visitedEmployees = employeeRepository.countVisited(employeeId);
        return allEmployees - visitedEmployees - 1;
    }

    private Employee resolveResponsibleOfSkills(Long responsibleId) {
        return (responsibleId == null) ? null :
                getById(responsibleId)
                        .orElseThrow(() -> new NotFoundException("employee.responsible.of.skills.not.found", responsibleId.toString()));
    }

    private Employee resolveResponsible(Long responsibleId) {
        return (responsibleId == null) ? null :
                getById(responsibleId)
                        .orElseThrow(() -> new NotFoundException("employee.responsible.not.found", responsibleId.toString()));
    }

    private void buildAndSendMessage(EmployeeCreateDTO admin) {
        mailService.sendEmail(fromEmailAddress, admin.getUsername(), WelcomeEmailHolder.TOPIC, buildBody(admin));
    }

    private String buildBody(EmployeeCreateDTO admin) {
        StringBuilder builder = new StringBuilder();
        if (admin.isWelcomeMessage()) {
            builder.append(WelcomeEmailHolder.WELCOME_MESSAGE).append("\n\n");
        }
        builder.append(String.format(WelcomeEmailHolder.CREDENTIALS, admin.getUsername(), admin.getPassword()));
        builder.append("\n\n");
        builder.append(WelcomeEmailHolder.FOOTER);
        return builder.toString();
    }
}
