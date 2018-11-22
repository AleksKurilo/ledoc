package dk.ledocsystem.data.repository;

import dk.ledocsystem.data.model.email_notifications.EmailNotification;
import dk.ledocsystem.data.model.email_notifications.EmailNotificationStatus;
import org.springframework.data.repository.CrudRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface EmailNotificationRepository extends CrudRepository<EmailNotification, Long> {

    List<EmailNotification> findTop100ByStatus(EmailNotificationStatus status);

    @Transactional
    default void updateStatus(EmailNotification emailNotification, EmailNotificationStatus status) {
        emailNotification.setStatus(status);
        save(emailNotification);
    }
}
