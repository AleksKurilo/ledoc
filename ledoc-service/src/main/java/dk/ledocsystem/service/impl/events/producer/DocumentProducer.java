package dk.ledocsystem.service.impl.events.producer;

import com.google.common.collect.ImmutableMap;
import dk.ledocsystem.data.model.document.Document;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.logging.LogType;
import dk.ledocsystem.service.impl.events.event.EntityEvents;
import dk.ledocsystem.service.impl.events.event.NotificationEvents;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class DocumentProducer {

    private final ApplicationEventPublisher publisher;

    public void create(Document document, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents(document, loggedInEmployee, LogType.Create));
        publisher.publishEvent(new NotificationEvents(loggedInEmployee.getUsername(), "document_created"));
        publisher.publishEvent(new NotificationEvents(document.getResponsible().getUsername(), "document_created"));
    }

    public void read(Document document, Employee loggedInEmployee, final boolean saveLog) {
        publisher.publishEvent(new EntityEvents(document, loggedInEmployee, LogType.Read, saveLog));
    }

    public void edit(Document document, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents(document, loggedInEmployee, LogType.Edit));
        publisher.publishEvent(new NotificationEvents(document.getResponsible().getUsername(), "document_edited"));

    }

    public void review(Document document, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents(document, loggedInEmployee, LogType.Review));
    }

    public void archive(Document document, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents(document, loggedInEmployee, LogType.Archive));
    }

    public void unarchive(Document document, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents(document, loggedInEmployee, LogType.Unarchive));
    }

    public void follow(Document document, Employee follower, boolean forced, boolean followed) {
        if (forced) {
            Map<String, Object> model = ImmutableMap.<String, Object>builder()
                    .put("document", document.getName())
                    .build();
            if (followed) {
                publisher.publishEvent(new NotificationEvents(follower.getUsername(), "document_follow_forced", model));
            } else {
                publisher.publishEvent(new NotificationEvents(follower.getUsername(), "document_unfollow_forced", model));
            }
        }
    }
}
