package dk.ledocsystem.service.api;

import com.querydsl.core.types.Predicate;
import dk.ledocsystem.data.model.document.Document;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.logging.DocumentLog;
import dk.ledocsystem.data.model.logging.EquipmentLog;
import dk.ledocsystem.data.model.logging.LogType;
import dk.ledocsystem.service.api.dto.inbound.LogsDTO;

public interface DocumentLogService extends AbstractLogService {

    /**
     * @param loggedInEmployee - employee who performed an action
     * @param document         - affected equipment
     * @param logType          - the type of action
     * @return Newly created {@link EquipmentLog}
     */
    DocumentLog createLog(Employee loggedInEmployee, Document document, LogType logType);

    /**
     * Returns the required log information to display
     *
     * @param documentId - id of target equipment
     * @param predicate
     * @return Name of employee and list of log properties
     */
    LogsDTO getAllDocumentLogs(Long documentId, Predicate predicate);
}
