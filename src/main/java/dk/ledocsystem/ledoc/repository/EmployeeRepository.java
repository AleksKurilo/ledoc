package dk.ledocsystem.ledoc.repository;

import dk.ledocsystem.ledoc.model.Employee;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {

    /**
     * Assigns the admin authorities to the {@link Employee} with the given ID.
     *
     * @param employeeId ID of the {@link Employee}
     */
    @Procedure("set_admin_authorities")
    void setAdminAuthorities(@Param("employee_id") Long employeeId);
}
