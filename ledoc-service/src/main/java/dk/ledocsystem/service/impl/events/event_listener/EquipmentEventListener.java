package dk.ledocsystem.service.impl.events.event_listener;

import dk.ledocsystem.data.model.email_notifications.EmailNotification;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.equipment.Equipment;
import dk.ledocsystem.data.model.logging.LogType;
import dk.ledocsystem.data.repository.EmailNotificationRepository;
import dk.ledocsystem.service.api.EquipmentLogService;
import dk.ledocsystem.service.impl.events.event.EntityEvents;
import dk.ledocsystem.service.impl.events.event.MonitoringEvents;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EquipmentEventListener {
    private final EquipmentLogService equipmentLogService;
    private final EmailNotificationRepository emailNotificationRepository;

    @EventListener(condition = "#event.saveLog")
    public void onApplicationEvent(EntityEvents<Equipment> event) {
        Equipment equipment = event.getSource();
        Employee loggedInEmployee = event.getLoggedInEmployee();
        equipmentLogService.createLog(loggedInEmployee, equipment, event.getLogType());

        if (event.getLogType().equals(LogType.Create)) {
            EmailNotification notificationForCreator = new EmailNotification(loggedInEmployee.getUsername(), "equipment_created");
            EmailNotification notificationForResponsible = new EmailNotification(equipment.getResponsible().getUsername(), "equipment_created");
            emailNotificationRepository.save(notificationForCreator);
            emailNotificationRepository.save(notificationForResponsible);
        }
        if (event.getLogType().equals(LogType.Edit)) {
            EmailNotification notification = new EmailNotification(equipment.getResponsible().getUsername(), "equipment_edited");
            emailNotificationRepository.save(notification);
        }
    }

    @EventListener
    public void onMonitoringEvent(MonitoringEvents<Equipment> event) {
        if (event.isForced()) {
            if (event.isFollowed()) {
                EmailNotification notification = new EmailNotification(event.getFollower().getUsername(), "equipment_follow_forced");
                emailNotificationRepository.save(notification);
            } else {
                EmailNotification notification = new EmailNotification(event.getFollower().getUsername(), "equipment_unfollow_forced");
                emailNotificationRepository.save(notification);
            }
        }
    }
}
