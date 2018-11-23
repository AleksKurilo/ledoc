package dk.ledocsystem.service.events.producer;


import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.equipment.Equipment;
import dk.ledocsystem.data.model.logging.LogType;
import dk.ledocsystem.service.events.event.EntityEvents;
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
