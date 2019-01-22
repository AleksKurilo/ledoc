package dk.ledocsystem.service.impl.events.event_listener;

import dk.ledocsystem.data.model.document.Document;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.logging.DocumentEditDetails;
import dk.ledocsystem.service.api.DocumentLogService;
import dk.ledocsystem.service.impl.events.event.EditEvent;
import dk.ledocsystem.service.impl.events.event.EntityEvents;
import dk.ledocsystem.service.impl.utils.diff.SingleDiff;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class DocumentEventListener {

    private final DocumentLogService documentLogService;

    @Async
    @TransactionalEventListener(condition = "#event.saveLog and #event.logType != T(dk.ledocsystem.data.model.logging.LogType).Edit")
    public void onApplicationEvent(EntityEvents<Document> event) {
        Employee loggedInEmployee = event.getLoggedInEmployee();
        Document document = event.getSource();

        documentLogService.createLog(loggedInEmployee, document, event.getLogType());
    }

    @Async
    @TransactionalEventListener
    public void onEditEvent(EditEvent<Document> event) {
        Employee loggedInEmployee = event.getLoggedInEmployee();
        Document document = event.getSource();

        documentLogService.createEditLog(loggedInEmployee, document, convertDiff(event.getDiffList()));
    }

    private List<DocumentEditDetails> convertDiff(List<SingleDiff> diffList) {
        return diffList.stream()
                .map(diff -> new DocumentEditDetails(diff.getProperty(), diff.getPreviousValue(), diff.getCurrentValue()))
                .collect(Collectors.toList());
    }
}
