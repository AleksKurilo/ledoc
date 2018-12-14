package dk.ledocsystem.service.impl.events.event_listener;

import dk.ledocsystem.data.model.document.Document;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.service.api.DocumentLogService;
import dk.ledocsystem.service.impl.events.event.EntityEvents;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class DocumentEventListener {

    private final DocumentLogService documentLogService;

    @Async
    @TransactionalEventListener(condition = "#event.saveLog")
    public void onApplicationEvent(EntityEvents<Document> event) {
        Document document = event.getSource();
        Employee loggedInEmployee = event.getLoggedInEmployee();
        documentLogService.createLog(loggedInEmployee, document, event.getLogType());
    }
}
