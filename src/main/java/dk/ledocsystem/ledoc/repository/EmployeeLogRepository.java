package dk.ledocsystem.ledoc.repository;

import dk.ledocsystem.ledoc.model.logging.EmployeeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeLogRepository extends JpaRepository<EmployeeLog, Long> {

    /**
     * Get list of employees by affected employee.
     *
     * @param employeeId The ID of affected employee.
     */
    List<EmployeeLog> getAllByTargetEmployeeId(Long employeeId);

}
