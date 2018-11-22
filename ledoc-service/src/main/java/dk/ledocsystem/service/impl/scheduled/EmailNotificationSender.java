package dk.ledocsystem.service.impl.scheduled;

import com.google.api.client.http.HttpResponse;
import dk.ledocsystem.data.model.email_notifications.EmailNotification;
import dk.ledocsystem.data.model.email_notifications.EmailNotificationStatus;
import dk.ledocsystem.data.repository.EmailNotificationRepository;
import dk.ledocsystem.service.api.EmailTemplateService;
import dk.ledocsystem.service.api.SimpleMailService;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import org.pmw.tinylog.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
class EmailNotificationSender {
    private static final int MAX_RETRIES = 3;

    private final EmailNotificationRepository emailNotificationRepository;
    private final EmailTemplateService emailTemplateService;
    private final SimpleMailService simpleMailService;

//    @Scheduled(initialDelay = 10000, fixedDelay = 10000)
    public void sendNotificationsScheduled() {
        List<EmailNotification> newNotifications = emailNotificationRepository.findTop100ByStatus(EmailNotificationStatus.NEW);

        for (EmailNotification newNotification : newNotifications) {
            try {
                ListenableFuture<HttpResponse> result = sendNotificationScheduled(newNotification);
                result.addCallback((result1 -> markAsSend(newNotification)), ex -> sendingFailed(newNotification, ex));
            } catch (Exception e) {
                Logger.error(e);
                emailNotificationRepository.updateStatus(newNotification, EmailNotificationStatus.INVALID_TEMPLATE);
            }
        }
    }

    private ListenableFuture<HttpResponse> sendNotificationScheduled(EmailNotification emailNotification)
            throws IOException, TemplateException {
        String emailKey = emailNotification.getEmailKey();
        Map<String, Object> model = emailNotification.getModel();

        EmailTemplateService.EmailTemplate template = emailTemplateService.getTemplateLocalized(emailKey);
        String html = template.parseTemplate(model);
        emailNotificationRepository.updateStatus(emailNotification, EmailNotificationStatus.PROCESSING);
        return simpleMailService.sendMimeMessage(emailNotification.getRecipient(), template.getSubject(), html);
    }

    private void markAsSend(EmailNotification emailNotification) {
        emailNotificationRepository.updateStatus(emailNotification, EmailNotificationStatus.SENT);
    }

    private void sendingFailed(EmailNotification emailNotification, Throwable reason) {
        Logger.error(reason);
        emailNotification.setRetries((short) (emailNotification.getRetries() + 1));
        if (emailNotification.getRetries() >= MAX_RETRIES) {
            emailNotification.setStatus(EmailNotificationStatus.FAILED);
        } else {
            emailNotification.setStatus(EmailNotificationStatus.NEW);
        }
        emailNotificationRepository.save(emailNotification);
    }
}
