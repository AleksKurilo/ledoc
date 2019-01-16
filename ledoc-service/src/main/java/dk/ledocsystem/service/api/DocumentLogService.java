package dk.ledocsystem.service.api;

import dk.ledocsystem.data.model.document.Document;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.logging.LogType;

public interface DocumentLogService extends AbstractLogService {

    /**
     * @param loggedInEmployee employee who performed an action
     * @param document         affected equipment
     * @param logType          the type of action
     */
    void createLog(Employee loggedInEmployee, Document document, LogType logType);
}
