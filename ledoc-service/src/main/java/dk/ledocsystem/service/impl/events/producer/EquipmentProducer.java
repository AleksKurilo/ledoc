package dk.ledocsystem.service.impl.events.producer;


import com.google.common.collect.ImmutableMap;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.equipment.Equipment;
import dk.ledocsystem.data.model.logging.LogType;
import dk.ledocsystem.service.impl.events.event.EntityEvents;
import dk.ledocsystem.service.impl.events.event.NotificationEvents;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class EquipmentProducer {
    private final ApplicationEventPublisher publisher;

    public void create(Equipment equipment, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents(equipment, loggedInEmployee, LogType.Create));
        publisher.publishEvent(new NotificationEvents(loggedInEmployee.getUsername(), "equipment_created"));
        publisher.publishEvent(new NotificationEvents(equipment.getResponsible().getUsername(), "equipment_created"));
    }

    public void read(Equipment equipment, Employee loggedInEmployee, final boolean saveLog) {
        publisher.publishEvent(new EntityEvents(equipment, loggedInEmployee, LogType.Read, saveLog));
    }

    public void edit(Equipment equipment, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents(equipment, loggedInEmployee, LogType.Edit));
        publisher.publishEvent(new NotificationEvents(equipment.getResponsible().getUsername(), "equipment_edited"));

    }

    public void review(Equipment equipment, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents(equipment, loggedInEmployee, LogType.Review));
    }

    public void archive(Equipment equipment, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents(equipment, loggedInEmployee, LogType.Archive));
    }

    public void unarchive(Equipment equipment, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents(equipment, loggedInEmployee, LogType.Unarchive));
    }

    public void follow(Equipment equipment, Employee follower, boolean forced, boolean followed) {
        if (forced) {
            Map<String, Object> model = ImmutableMap.<String, Object>builder()
                    .put("equipment", equipment.getName())
                    .build();
            if (followed) {
                publisher.publishEvent(new NotificationEvents(follower.getUsername(), "equipment_follow_forced", model));
            } else {
                publisher.publishEvent(new NotificationEvents(follower.getUsername(), "equipment_unfollow_forced", model));
            }
        }
    }
}
