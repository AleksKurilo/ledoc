package dk.ledocsystem.ledoc.service;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.ledoc.dto.AbstractLogDTO;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.model.logging.EmployeeLog;
import dk.ledocsystem.ledoc.model.logging.LogType;

import java.util.List;

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
     * @return List of log properties
     */
    List<AbstractLogDTO> getAllEmployeeLogs(Predicate predicate);
}
