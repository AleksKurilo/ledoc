package dk.ledocsystem.ledoc.service.impl;

import dk.ledocsystem.ledoc.config.security.UserAuthorities;
import dk.ledocsystem.ledoc.dto.EmployeeDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.repository.EmployeeRepository;
import dk.ledocsystem.ledoc.dto.projections.EmployeeNames;
import dk.ledocsystem.ledoc.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }

    @Override
    public Optional<Employee> getById(Long id) {
        return employeeRepository.findById(id);
    }

    @Override
    public Employee createEmployee(EmployeeDTO employeeDTO, Customer customer) {
        employeeDTO.setPassword(passwordEncoder.encode(employeeDTO.getPassword()));
        Employee employee = new Employee(employeeDTO);
        employee.setCustomer(customer);
        return employeeRepository.save(employee);
    }

    @Transactional
    @Override
    public Employee updateEmployee(Long employeeId, EmployeeDTO employeeDTO) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("employee.id.not.found", employeeId.toString()));
        employee.updateProperties(employeeDTO);
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
        long allEmployees = employeeRepository.countByCustomerIdAndArchivedTrue(customerId);
        long visitedEmployees = employeeRepository.countVisited(employeeId);
        return allEmployees - visitedEmployees;
    }
}
