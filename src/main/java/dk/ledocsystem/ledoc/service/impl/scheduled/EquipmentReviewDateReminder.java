package dk.ledocsystem.ledoc.service.impl.scheduled;

import com.google.common.collect.ImmutableMap;
import dk.ledocsystem.ledoc.model.email_notifications.EmailNotification;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.model.equipment.Equipment;
import dk.ledocsystem.ledoc.repository.EmailNotificationRepository;
import dk.ledocsystem.ledoc.service.EquipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
class EquipmentReviewDateReminder {

    private final EquipmentService equipmentService;
    private final EmailNotificationRepository emailNotificationRepository;

    @Scheduled(cron = "0 0 4 * * *") // every weekday at 4:00 am
    public void checkReviewDates() {
        for (Equipment equipment : equipmentService.getAllForReview()) {
            checkReviewDate(equipment.getNextReviewDate(), equipment);
        }
    }

    private void checkReviewDate(LocalDate reviewDate, Equipment equipment) {
        LocalDate today = LocalDate.now();
        if (today.until(reviewDate).getDays() == 14) {
            sendReviewIn2Weeks(equipment);
        } else if (today.equals(reviewDate)) {
            sendReviewToday(equipment);
        }
    }

    private void sendReviewIn2Weeks(Equipment equipment) {
        Employee responsible = equipment.getResponsible();
        Map<String, Object> model = ImmutableMap.of("equipmentName", equipment.getName());
        EmailNotification notification = new EmailNotification(responsible.getUsername(),
                "equipment_review_in_2_weeks", model);

        emailNotificationRepository.save(notification);
    }

    private void sendReviewToday(Equipment equipment) {
        Employee responsible = equipment.getResponsible();
        Map<String, Object> model = ImmutableMap.of("equipmentName", equipment.getName());
        EmailNotification notification = new EmailNotification(responsible.getUsername(),
                "equipment_review_today", model);

        emailNotificationRepository.save(notification);
    }
}
