package dk.ledocsystem.ledoc.service;

import dk.ledocsystem.ledoc.dto.EmployeeDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.Employee;
import dk.ledocsystem.ledoc.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Creates new {@link Employee}, using the data from {@code employeeDTO}, and assigns {@code customer} to it.
     *
     * @param employeeDTO Employee properties
     * @param customer    Customer - the owner of {@link Employee}
     * @return Newly created {@link Employee}
     */
    public Employee createEmployee(EmployeeDTO employeeDTO, Customer customer) {
        employeeDTO.setPassword(passwordEncoder.encode(employeeDTO.getPassword()));
        Employee employee = new Employee(employeeDTO);
        employee.setCustomer(customer);
        return employeeRepository.save(employee);
    }

    /**
     * Updates the properties of the employee with the given ID with properties of {@code employeeDTO}.
     *
     * @param employeeId  ID of the employee
     * @param employeeDTO New properties of the employee
     * @return Updated {@link Employee}
     */
    @Transactional
    public Employee updateEmployee(Long employeeId, EmployeeDTO employeeDTO) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException(Employee.class, employeeId));
        employee.updateProperties(employeeDTO);
        return employeeRepository.save(employee);
    }

    /**
     * Deletes employees with the given IDs.
     *
     * @param employeeIds The collection of employee IDs.
     */
    @Transactional
    public void deleteByIds(Collection<Long> employeeIds) {
        employeeRepository.deleteByIdIn(employeeIds);
    }
}
