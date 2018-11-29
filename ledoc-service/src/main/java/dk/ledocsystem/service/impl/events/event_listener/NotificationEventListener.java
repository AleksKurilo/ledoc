package dk.ledocsystem.service.impl.events.event_listener;

import dk.ledocsystem.data.model.email_notifications.EmailNotification;
import dk.ledocsystem.data.repository.EmailNotificationRepository;
import dk.ledocsystem.service.impl.events.event.NotificationEvents;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
public class NotificationEventListener {
    private final EmailNotificationRepository emailNotificationRepository;

    @Async
    @TransactionalEventListener
    public void onApplicationEvent(NotificationEvents event) {
        EmailNotification notification = new EmailNotification(event.getRecipient(), event.getEmailKey(), event.getModel());
        emailNotificationRepository.save(notification);
    }
}
