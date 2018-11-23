package dk.ledocsystem.service.events.event_listener;

import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.service.api.EmployeeLogService;
import dk.ledocsystem.service.events.event.EntityEvents;
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
