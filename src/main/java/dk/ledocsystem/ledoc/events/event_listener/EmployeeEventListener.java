package dk.ledocsystem.ledoc.events.event_listener;

import dk.ledocsystem.ledoc.events.event.EntityEvents;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.service.EmployeeLogService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EmployeeEventListener {
    private final EmployeeLogService employeeLogService;
    @EventListener(condition = "#event.saveLog")
    public void onApplicationEvent(EntityEvents<Employee> event) {
        employeeLogService.createLog(event.getLoggedInEmployee(), event.getSource(), event.getLogType());
    }
}
