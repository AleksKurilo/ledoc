package dk.ledocsystem.service.impl.events.producer;


import com.google.common.collect.ImmutableMap;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.equipment.Equipment;
import dk.ledocsystem.data.model.logging.LogType;
import dk.ledocsystem.service.impl.events.event.EditEvent;
import dk.ledocsystem.service.impl.events.event.EntityEvents;
import dk.ledocsystem.service.impl.events.event.NotificationEvents;
import dk.ledocsystem.service.impl.utils.diff.DiffFinder;
import dk.ledocsystem.service.impl.utils.diff.SingleDiff;
import dk.ledocsystem.service.impl.utils.diff.comparators.EquipmentComparator;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EquipmentProducer {
    private final ApplicationEventPublisher publisher;
    private final DiffFinder diffFinder;

    public void create(Equipment equipment, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents<>(equipment, loggedInEmployee, LogType.Create));
        publisher.publishEvent(new NotificationEvents(loggedInEmployee.getUsername(), "equipment_created"));
        publisher.publishEvent(new NotificationEvents(equipment.getResponsible().getUsername(), "equipment_created"));
    }

    public void read(Equipment equipment, Employee loggedInEmployee, boolean saveLog) {
        publisher.publishEvent(new EntityEvents<>(equipment, loggedInEmployee, LogType.Read, saveLog));
    }

    public void edit(Equipment equipmentBeforeEdit, Equipment equipmentAfterEdit, Employee loggedInEmployee) {
        List<SingleDiff> diffList = diffFinder.findDiff(equipmentBeforeEdit, equipmentAfterEdit, EquipmentComparator.INSTANCE);
        publisher.publishEvent(new EditEvent<>(equipmentAfterEdit, loggedInEmployee, diffList));
        if (!equipmentAfterEdit.getResponsible().equals(equipmentBeforeEdit.getResponsible())) {
            publisher.publishEvent(new NotificationEvents(equipmentAfterEdit.getResponsible().getUsername(),
                    "equipment_responsible_changed", ImmutableMap.of("equipmentName", equipmentAfterEdit.getName())));
        }
    }

    public void review(Equipment equipment, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents<>(equipment, loggedInEmployee, LogType.Review));
    }

    public void archive(Equipment equipment, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents<>(equipment, loggedInEmployee, LogType.Archive));
    }

    public void unarchive(Equipment equipment, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents<>(equipment, loggedInEmployee, LogType.Unarchive));
    }

    public void follow(Equipment equipment, Employee follower, boolean forced, boolean followed) {
        if (forced) {
            Map<String, Object> model = ImmutableMap.of("equipment", equipment.getName());
            if (followed) {
                publisher.publishEvent(new NotificationEvents(follower.getUsername(), "equipment_follow_forced", model));
            } else {
                publisher.publishEvent(new NotificationEvents(follower.getUsername(), "equipment_unfollow_forced", model));
            }
        }
    }
}
