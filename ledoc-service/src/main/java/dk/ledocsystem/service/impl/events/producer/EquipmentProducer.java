package dk.ledocsystem.service.impl.events.producer;


import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.equipment.Equipment;
import dk.ledocsystem.data.model.logging.LogType;
import dk.ledocsystem.service.impl.events.event.EntityEvents;
import dk.ledocsystem.service.impl.events.event.MonitoringEvents;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EquipmentProducer {
    private final ApplicationEventPublisher publisher;

    public void create(Equipment equipment, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents(equipment, loggedInEmployee, LogType.Create));
    }

    public void read(Equipment equipment, Employee loggedInEmployee, final boolean saveLog) {
        publisher.publishEvent(new EntityEvents(equipment, loggedInEmployee, LogType.Read, saveLog));
    }

    public void edit(Equipment equipment, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents(equipment, loggedInEmployee, LogType.Edit));

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
        publisher.publishEvent(new MonitoringEvents(equipment, follower, forced, followed));
    }
}
