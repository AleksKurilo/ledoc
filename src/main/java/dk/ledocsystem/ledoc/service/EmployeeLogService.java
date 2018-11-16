package dk.ledocsystem.ledoc.service;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.dto.EmployeeLogDTO;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.model.logging.EmployeeLog;
import dk.ledocsystem.ledoc.model.logging.LogType;

public interface EmployeeLogService extends AbstractLogService{

    /**
     *
     * @param loggedInEmployee - employee who performed and action
     * @param targetEmployee - affected employee
     * @param logType - the type of action
     * @return Newly created {@link EmployeeLog}
     */
    EmployeeLog createLog(Employee loggedInEmployee, Employee targetEmployee, LogType logType);

    /**
     * Returns the required log information to display
     * @param predicate
     * @return Name of employee and list of log properties
     */
    EmployeeLogDTO getAllEmployeeLogs(Long employeeId, Predicate predicate);
}
