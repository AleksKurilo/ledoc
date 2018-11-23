package dk.ledocsystem.ledoc.events.producer;


import dk.ledocsystem.ledoc.events.event.EntityEvents;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.model.equipment.Equipment;
import dk.ledocsystem.ledoc.model.logging.LogType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class EquipmentProducer {
    @Autowired
    final ApplicationEventPublisher publisher;

    public EquipmentProducer(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

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
}
