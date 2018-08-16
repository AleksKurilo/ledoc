package dk.ledocsystem.ledoc.service.impl;

import dk.ledocsystem.ledoc.config.security.UserAuthorities;
import dk.ledocsystem.ledoc.dto.employee.EmployeeCreateDTO;
import dk.ledocsystem.ledoc.dto.employee.EmployeeEditDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.repository.EmployeeRepository;
import dk.ledocsystem.ledoc.dto.projections.EmployeeNames;
import dk.ledocsystem.ledoc.service.CustomerService;
import dk.ledocsystem.ledoc.service.EmployeeService;
import dk.ledocsystem.ledoc.service.SimpleMailService;
import dk.ledocsystem.ledoc.util.BeanCopyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor = @__({@Lazy}))
class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;
    private final SimpleMailService mailService;

    @Value("${spring.mail.username}")
    private String fromEmailAddress;

    @Override
    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }

    @Override
    public Optional<Employee> getById(Long id) {
        return employeeRepository.findById(id);
    }

    @Transactional
    @Override
    public Employee createEmployee(EmployeeCreateDTO employeeCreateDTO) {
        return createEmployee(employeeCreateDTO, customerService.getCurrentCustomerReference());
    }

    @Transactional
    @Override
    public Employee createEmployee(EmployeeCreateDTO employeeCreateDTO, Customer customer) {
        Employee employee = new Employee();
        BeanCopyUtils.copyProperties(employeeCreateDTO, employee);
        employee.setPassword(passwordEncoder.encode(employee.getPassword()));
        employee.setCustomer(customer);

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
    public Employee updateEmployee(Long employeeId, EmployeeEditDTO employeeEditDTO) {
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
    public void deleteById(Long id) {
        employeeRepository.deleteById(id);
    }

    @Transactional
    @Override
    public void deleteByIds(Collection<Long> employeeIds) {
        employeeRepository.deleteByIdIn(employeeIds);
    }

    @Override
    public void changePassword(String username, String newPassword) {
        employeeRepository.changePassword(username, newPassword);
    }

    @Transactional
    @Override
    public void addAuthorities(Long employeeId, UserAuthorities authorities) {
        employeeRepository.addAuthorities(employeeId, authorities);
    }

    @Override
    public Optional<Employee> getByUsername(String username) {
        return employeeRepository.findByUsername(username);
    }

    @Override
    public boolean existsByUsername(String username) {
        return employeeRepository.existsByUsername(username);
    }

    @Override
    public List<EmployeeNames> getAllByRole(UserAuthorities authorities) {
        return employeeRepository.findAllByAuthoritiesContains(authorities);
    }

    @Override
    public long countNewEmployees(Long customerId, Long employeeId) {
        long allEmployees = employeeRepository.countByCustomerIdAndArchivedFalse(customerId);
        long visitedEmployees = employeeRepository.countVisited(employeeId);
        return allEmployees - visitedEmployees - 1;
    }

    private Employee resolveResponsibleOfSkills(Long responsibleId) {
        return (responsibleId == null) ? null :
                getById(responsibleId)
                        .orElseThrow(() -> new NotFoundException("employee.responsible.of.skills.not.found", responsibleId.toString()));
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
