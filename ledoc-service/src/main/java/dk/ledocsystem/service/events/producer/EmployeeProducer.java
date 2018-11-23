package dk.ledocsystem.service.events.producer;

import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.logging.LogType;
import dk.ledocsystem.service.events.event.EntityEvents;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class EmployeeProducer {
    @Autowired
    final ApplicationEventPublisher publisher;

    public EmployeeProducer(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void create(Employee employee, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents(employee, loggedInEmployee, LogType.Create));
    }


    public void read(Employee employee, Employee loggedInEmployee, final boolean saveLog) {
        publisher.publishEvent(new EntityEvents(employee, loggedInEmployee, LogType.Read, saveLog));
    }

    public void edit(Employee employee, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents(employee, loggedInEmployee, LogType.Edit));
    }

    public void review(Employee employee, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents(employee, loggedInEmployee, LogType.Review));
    }

    public void archive(Employee employee, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents(employee, loggedInEmployee, LogType.Archive));
    }

    public void unarchive(Employee employee, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents(employee, loggedInEmployee, LogType.Unarchive));
    }
}
