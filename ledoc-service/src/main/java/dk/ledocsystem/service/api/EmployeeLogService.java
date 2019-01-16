package dk.ledocsystem.service.api;

import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.logging.EmployeeEditDetails;
import dk.ledocsystem.data.model.logging.LogType;

import java.util.List;

public interface EmployeeLogService extends AbstractLogService {

    /**
     * @param loggedInEmployee employee who performed an action
     * @param targetEmployee   affected employee
     * @param logType          the type of action
     */
    void createLog(Employee loggedInEmployee, Employee targetEmployee, LogType logType);

    /**
     * @param loggedInEmployee employee who performed an action
     * @param targetEmployee   affected employee
     * @param editDetails      List of edit details
     */
    void createEditLog(Employee loggedInEmployee, Employee targetEmployee, List<EmployeeEditDetails> editDetails);
}
