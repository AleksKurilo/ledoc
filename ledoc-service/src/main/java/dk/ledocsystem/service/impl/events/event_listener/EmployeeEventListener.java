package dk.ledocsystem.service.impl.events.event_listener;

import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.service.api.EmployeeLogService;
import dk.ledocsystem.service.impl.events.event.EditEvent;
import dk.ledocsystem.service.impl.events.event.EntityEvents;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@AllArgsConstructor
public class EmployeeEventListener {
    private final EmployeeLogService employeeLogService;

    @Async
    @TransactionalEventListener(condition = "#event.saveLog and #event.logType != T(dk.ledocsystem.data.model.logging.LogType).Edit")
    public void onApplicationEvent(EntityEvents<Employee> event) {
        Employee loggedInEmployee = event.getLoggedInEmployee();
        Employee employee = event.getSource();

        employeeLogService.createLog(loggedInEmployee, employee, event.getLogType());
    }

    @Async
    @TransactionalEventListener
    public void onEditEvent(EditEvent<Employee> event) {
        Employee loggedInEmployee = event.getLoggedInEmployee();
        Employee employee = event.getSource();

        employeeLogService.createEditLog(loggedInEmployee, employee, event.getEditDetails());
    }
}
