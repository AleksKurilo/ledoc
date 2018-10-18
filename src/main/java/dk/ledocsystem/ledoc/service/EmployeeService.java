package dk.ledocsystem.ledoc.service;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.config.security.UserAuthorities;
import dk.ledocsystem.ledoc.dto.ArchivedStatusDTO;
import dk.ledocsystem.ledoc.dto.employee.EmployeeCreateDTO;
import dk.ledocsystem.ledoc.dto.employee.EmployeeDTO;
import dk.ledocsystem.ledoc.dto.review.ReviewDTO;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.service.dto.EmployeePreviewDTO;
import dk.ledocsystem.ledoc.service.dto.GetEmployeeDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EmployeeService extends CustomerBasedDomainService<Employee> {

    /**
     * Creates new {@link Employee}, using the data from {@code employeeCreateDTO}, and assigns {@code customer} to it.
     *
     * @param employeeCreateDTO Employee properties
     * @param customer          Customer - the owner of employee
     * @return Newly created {@link Employee}
     */
    Employee createEmployee(EmployeeCreateDTO employeeCreateDTO, Customer customer);

    /**
     * Creates new {@link Employee} point of contact, using the data from {@code employeeCreateDTO}.
     *
     * @param employeeCreateDTO Point of contact properties
     * @param customer          Customer - the owner of point of contact
     * @return Newly created {@link Employee}
     */
    Employee createPointOfContact(EmployeeCreateDTO employeeCreateDTO, Customer customer);

    /**
     * Updates the properties of the employee with the given ID with properties of {@code employeeCreateDTO}.
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

    /**
     * Changes the archived status according to data from {@code archivedStatusDTO}.
     */
    void changeArchivedStatus(Long employeeId, ArchivedStatusDTO archivedStatusDTO);

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

    /**
     * Performs review of the given employee.
     *
     * @param employeeId ID of the employee
     * @param reviewDTO  Information about performed review
     */
    void performReview(Long employeeId, ReviewDTO reviewDTO);

    /**
     * @return All employees eligible for review
     */
    List<Employee> getAllForReview();

    boolean existsByUsername(String username);

    Page<Employee> getNewEmployees(Long userId, Pageable pageable);

    Page<Employee> getNewEmployees(Long userId, Pageable pageable, Predicate predicate);

    // TODO This is shit.
    // TODO Replace it with getById during service layer separation process.
    Optional<GetEmployeeDTO> getEmployeeDtoById(Long employeeId);

    Optional<EmployeePreviewDTO> getPreviewDtoById(Long employeeId);

    /**
     * @return A proxy, whose state can be lazily populated upon access until the end of current transaction.
     * @see javax.persistence.EntityManager#getReference(Class, Object)
     */
    Employee getCurrentUserReference();
}
