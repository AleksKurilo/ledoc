package dk.ledocsystem.ledoc.service;

import dk.ledocsystem.ledoc.dto.EmployeeDTO;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.employee.Employee;

import java.util.Optional;

public interface EmployeeService extends DomainService<Employee> {

    /**
     * Creates new {@link Employee}, using the data from {@code employeeDTO}, and assigns {@code customer} to it.
     *
     * @param employeeDTO Employee properties
     * @param customer    Customer - the owner of {@link Employee}
     * @return Newly created {@link Employee}
     */
    Employee createEmployee(EmployeeDTO employeeDTO, Customer customer);

    /**
     * Updates the properties of the employee with the given ID with properties of {@code employeeDTO}.
     *
     * @param employeeId  ID of the employee
     * @param employeeDTO New properties of the employee
     * @return Updated {@link Employee}
     */
    Employee updateEmployee(Long employeeId, EmployeeDTO employeeDTO);

    /**
     * Changes password of the user with given email.
     *
     * @param username    Username identifying the user
     * @param newPassword New password
     */
    void changePassword(String username, String newPassword);

    Optional<Employee> getByUsername(String username);

    boolean existsByUsername(String username);
}
