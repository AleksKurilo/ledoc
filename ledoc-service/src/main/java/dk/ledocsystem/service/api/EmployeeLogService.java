package dk.ledocsystem.service.api;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.logging.EmployeeLog;
import dk.ledocsystem.data.model.logging.LogType;
import dk.ledocsystem.service.api.dto.outbound.LogsDTO;

public interface EmployeeLogService extends AbstractLogService {

    /**
     *
     * @param loggedInEmployee - employee who performed an action
     * @param targetEmployee - affected employee
     * @param logType - the type of action
     * @return Newly created {@link EmployeeLog}
     */
    EmployeeLog createLog(Employee loggedInEmployee, Employee targetEmployee, LogType logType);

    /**
     * Returns the required log information to display
     * @param employeeId - id of target employee
     * @param predicate
     * @return Name of employee and list of log properties
     */
    LogsDTO getAllEmployeeLogs(Long employeeId, Predicate predicate);
}
