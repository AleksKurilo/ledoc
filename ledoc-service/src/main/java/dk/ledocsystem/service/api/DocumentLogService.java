package dk.ledocsystem.service.api;

import dk.ledocsystem.data.model.document.Document;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.logging.DocumentEditDetails;
import dk.ledocsystem.data.model.logging.LogType;

import java.util.List;

public interface DocumentLogService extends AbstractLogService {

    /**
     * @param loggedInEmployee employee who performed an action
     * @param document         affected document
     * @param logType          the type of action
     */
    void createLog(Employee loggedInEmployee, Document document, LogType logType);

    /**
     * @param loggedInEmployee employee who performed an action
     * @param document         affected document
     * @param editDetails      List of edit details
     */
    void createEditLog(Employee loggedInEmployee, Document document, List<DocumentEditDetails> editDetails);
}
