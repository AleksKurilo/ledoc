package dk.ledocsystem.ledoc.repository;

import dk.ledocsystem.ledoc.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * Assigns the admin authorities to the {@link Employee} with the given ID.
     *
     * @param employeeId ID of the {@link Employee}
     */
    @Procedure("set_admin_authorities")
    void setAdminAuthorities(@Param("employee_id") Long employeeId);

    /**
     * @param email Email
     * @return {@link Optional} with {@link Employee employee} with provided email or empty Optional if none found.
     */
    Optional<Employee> findByEmail(String email);

    /**
     * Deletes employees with the given IDs.
     *
     * @param ids The collection of employee IDs.
     */
    @Modifying
    @Query(value = "delete from main.employees e where e.id in ?1", nativeQuery = true)
    void deleteByIdIn(Collection<Long> ids);
}
