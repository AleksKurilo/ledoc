package dk.ledocsystem.ledoc.service;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.config.security.UserAuthorities;
import dk.ledocsystem.ledoc.dto.employee.EmployeeCreateDTO;
import dk.ledocsystem.ledoc.dto.employee.EmployeeEditDTO;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.dto.projections.EmployeeNames;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;

public interface EmployeeService extends DomainService<Employee> {

    /**
     * Creates new {@link Employee}, using the data from {@code employeeCreateDTO}, and assigns {@code customer} to it.
     *
     * @param employeeCreateDTO Employee properties
     * @param customer          Customer - the owner of {@link Employee}
     * @return Newly created {@link Employee}
     */
    Employee createEmployee(EmployeeCreateDTO employeeCreateDTO, Customer customer);

    /**
     * Creates new {@link Employee}, using the data from {@code employeeCreateDTO}.
     *
     * @param employeeCreateDTO Employee properties
     * @return Newly created {@link Employee}
     */
    Employee createEmployee(EmployeeCreateDTO employeeCreateDTO);

    /**
     * Creates new {@link Employee} point of contact, using the data from {@code employeeCreateDTO}.
     *
     * @param employeeCreateDTO Employee properties
     * @return Newly created {@link Employee}
     */
    Employee createPointOfContact(EmployeeCreateDTO employeeCreateDTO);

    /**
     * Updates the properties of the employee with the given ID with properties of {@code employeeCreateDTO}.
     *
     * @param employeeId      ID of the employee
     * @param employeeEditDTO New properties of the employee
     * @return Updated {@link Employee}
     */
    Employee updateEmployee(Long employeeId, EmployeeEditDTO employeeEditDTO);

    /**
     * Changes password of the user with given email.
     *
     * @param username    Username identifying the user
     * @param newPassword New password
     */
    void changePassword(String username, String newPassword);

    /**
     * Grants the provided {@link UserAuthorities authorities} to employee.
     *
     * @param employeeId  ID of the employee
     * @param authorities Authorities
     */
    void grantAuthorities(Long employeeId, UserAuthorities authorities);

    /**
     * Revokes the provided {@link UserAuthorities authorities} from employee.
     *
     * @param employeeId  ID of the employee
     * @param authorities Authorities
     */
    void revokeAuthorities(Long employeeId, UserAuthorities authorities);

    List<Employee> findAllById(Collection<Long> ids);

    /**
     * @return All employees eligible for review
     */
    List<Employee> getAllForReview();

    boolean existsByUsername(String username);

    Page<Employee> getNewEmployees(Pageable pageable);

    Page<Employee> getNewEmployees(Pageable pageable, Predicate predicate);

    long countNewEmployees(Long customerId, Long employeeId);

    /**
     * @return A proxy, whose state can be lazily populated upon access until the end of current transaction.
     * @see javax.persistence.EntityManager#getReference(Class, Object)
     */
    Employee getCurrentUserReference();

    List<EmployeeNames> getAllByRole(UserAuthorities authorities);
}
