package dk.ledocsystem.service.impl.events.producer;

import com.google.common.collect.ImmutableMap;
import dk.ledocsystem.data.model.document.Document;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.logging.LogType;
import dk.ledocsystem.service.impl.events.event.EditEvent;
import dk.ledocsystem.service.impl.events.event.EntityEvents;
import dk.ledocsystem.service.impl.events.event.NotificationEvents;
import dk.ledocsystem.service.impl.utils.diff.DiffFinder;
import dk.ledocsystem.service.impl.utils.diff.SingleDiff;
import dk.ledocsystem.service.impl.utils.diff.comparators.DocumentComparator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class DocumentProducer {

    private final ApplicationEventPublisher publisher;
    private final DiffFinder diffFinder;

    public void create(Document document, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents<>(document, loggedInEmployee, LogType.Create));
        publisher.publishEvent(new NotificationEvents(loggedInEmployee.getUsername(), "document_created"));
        publisher.publishEvent(new NotificationEvents(document.getResponsible().getUsername(), "document_created"));
    }

    public void read(Document document, Employee loggedInEmployee, boolean saveLog) {
        publisher.publishEvent(new EntityEvents<>(document, loggedInEmployee, LogType.Read, saveLog));
    }

    public void edit(Document documentBeforeEdit, Document documentAfterEdit, Employee loggedInEmployee) {
        List<SingleDiff> diffList = diffFinder.findDiff(documentBeforeEdit, documentAfterEdit, DocumentComparator.INSTANCE);
        publisher.publishEvent(new EditEvent<>(documentAfterEdit, loggedInEmployee, diffList));
        if (!documentAfterEdit.getResponsible().equals(documentBeforeEdit.getResponsible())) {
            publisher.publishEvent(new NotificationEvents(documentAfterEdit.getResponsible().getUsername(),
                    "document_responsible_changed", ImmutableMap.of("documentName", documentAfterEdit.getName())));
        }
    }

    public void review(Document document, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents<>(document, loggedInEmployee, LogType.Review));
    }

    public void archive(Document document, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents<>(document, loggedInEmployee, LogType.Archive));
    }

    public void unarchive(Document document, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents<>(document, loggedInEmployee, LogType.Unarchive));
    }

    public void follow(Document document, Employee follower, boolean forced, boolean followed) {
        if (forced) {
            Map<String, Object> model = ImmutableMap.of("documentName", document.getName());
            if (followed) {
                publisher.publishEvent(new NotificationEvents(follower.getUsername(), "document_follow_forced", model));
            } else {
                publisher.publishEvent(new NotificationEvents(follower.getUsername(), "document_unfollow_forced", model));
            }
        }
    }
}
