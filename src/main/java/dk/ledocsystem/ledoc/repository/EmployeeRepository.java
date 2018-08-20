package dk.ledocsystem.ledoc.repository;

import dk.ledocsystem.ledoc.config.security.UserAuthorities;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.dto.projections.EmployeeNames;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, LoggingRepository<Employee, Long> {

    /**
     * Assigns the provided authorities to {@link Employee} with the given ID.
     *
     * @param employeeId  Employee ID
     * @param authorities Authority object
     */
    @Modifying
    @Query(value = "INSERT INTO main.employee_authorities VALUES(:#{#employeeId}, :#{#authorities.code}) ON CONFLICT DO NOTHING",
            nativeQuery = true)
    void addAuthorities(@Param("employeeId") Long employeeId,
                        @Param("authorities") UserAuthorities authorities);

    /**
     * @param authorities {@link UserAuthorities authority}
     * @return All employees that have given authority
     */
    List<EmployeeNames> findAllByAuthoritiesContains(UserAuthorities authorities);

    /**
     * @param authorities {@link UserAuthorities authority}
     * @return All employees that have given authority and not archived
     */
    int countAllByAuthoritiesContainsAndArchivedIsFalse(UserAuthorities authorities);

    /**
     * @param username Username
     * @return {@link Optional} with {@link Employee employee} with provided username or empty Optional if none found.
     */
    Optional<Employee> findByUsername(String username);

    /**
     * @param username Username
     * @return {@code true} if there is user with provided name
     */
    boolean existsByUsername(String username);

    /**
     * Changes password of the user with given email.
     *
     * @param username    Username identifying the user
     * @param newPassword New password
     */
    @Modifying
    @Query("update Employee e set e.password = ?2 where e.username = ?1")
    void changePassword(String username, String newPassword);

    /**
     * Deletes employees with the given IDs.
     *
     * @param ids The collection of employee IDs.
     */
    @Modifying
    @Query("delete from Employee e where e.id in ?1")
    void deleteByIdIn(Collection<Long> ids);

    /**
     * Counts employees that are not archived and
     * has {@link dk.ledocsystem.ledoc.model.Customer} with provided ID.
     */
    long countByCustomerIdAndArchivedFalse(Long customerId);

    /**
     * @return All {@link Employee} employees that are not archived
     */
    List<Employee> findAllByArchivedFalse();

    /**
     * @return All {@link Employee} employees that are archived
     */
    List<Employee> findAllByArchivedTrue();

    /**
     * @param customerId customerId
     * @return All {@link Employee} eployees of current {@link dk.ledocsystem.ledoc.model.Customer} company
     */
    List<Employee> findAllByCustomer_Id(Long customerId);

    /**
     * @param customerId customerId
     * @return All {@link Employee} not archived eployees of current {@link dk.ledocsystem.ledoc.model.Customer} company
     */
    List<Employee> findAllByCustomer_IdAndArchivedIsFalse(Long customerId);

    /**
     * @param customerId customerId
     * @return All {@link Employee} archived in given {@link dk.ledocsystem.ledoc.model.Customer} company
     */
    List<Employee> findAllByCustomer_IdAndArchivedIsTrue(Long customerId);
}
