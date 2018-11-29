package dk.ledocsystem.service.impl.events.producer;

import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.logging.LogType;
import dk.ledocsystem.service.api.dto.inbound.employee.EmployeeCreateDTO;
import dk.ledocsystem.service.impl.events.event.EntityEvents;
import dk.ledocsystem.service.impl.events.event.MonitoringEvents;
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

    public void create(EmployeeCreateDTO employee, Employee loggedInEmployee) {
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

    public void follow(Employee employee, Employee follower, boolean forced, boolean followed) {
        publisher.publishEvent(new MonitoringEvents(employee, follower, forced, followed));
    }
}
