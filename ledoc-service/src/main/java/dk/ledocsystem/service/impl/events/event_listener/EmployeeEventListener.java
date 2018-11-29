package dk.ledocsystem.service.impl.events.event_listener;

import com.google.common.collect.ImmutableMap;
import dk.ledocsystem.data.model.email_notifications.EmailNotification;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.logging.LogType;
import dk.ledocsystem.data.repository.EmailNotificationRepository;
import dk.ledocsystem.service.api.EmployeeLogService;
import dk.ledocsystem.service.api.dto.inbound.employee.EmployeeCreateDTO;
import dk.ledocsystem.service.impl.events.event.EntityEvents;
import dk.ledocsystem.service.impl.events.event.MonitoringEvents;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class EmployeeEventListener {
    private final ModelMapper modelMapper;
    private final EmployeeLogService employeeLogService;
    private final EmailNotificationRepository emailNotificationRepository;

    @EventListener(condition = "#event.saveLog")
    public void onApplicationEvent(EntityEvents<Employee> event) {
        Employee loggedInEmployee = event.getLoggedInEmployee();
        Employee employee = event.getSource();

        employeeLogService.createLog(loggedInEmployee, employee, event.getLogType());

        if (event.getLogType().equals(LogType.Edit) && employee.getResponsible() != null) {
            EmailNotification notification = new EmailNotification(employee.getResponsible().getUsername(), "employee_edited");
            emailNotificationRepository.save(notification);
        }
    }

    @EventListener
    public void onCreateEmployeeEvent(EntityEvents<EmployeeCreateDTO> event) {
        EmployeeCreateDTO employeeCreateDTO = event.getSource();

        if (employeeCreateDTO.isWelcomeMessage()) {
            Map<String, Object> model = ImmutableMap.<String, Object>builder()
                    .put("username", employeeCreateDTO.getUsername())
                    .put("password", employeeCreateDTO.getPassword())
                    .build();
            EmailNotification welcomeMessage = new EmailNotification(employeeCreateDTO.getUsername(), "welcome", model);
            emailNotificationRepository.save(welcomeMessage);
        }

        Employee employee = modelMapper.map(employeeCreateDTO, Employee.class);

        if (employee.getResponsible() != null) {
            EmailNotification notificationForResponsible = new EmailNotification(employee.getResponsible().getUsername(), "employee_created");
            emailNotificationRepository.save(notificationForResponsible);
        }
    }

    @EventListener
    public void onMonitoringEvent(MonitoringEvents<Employee> event) {
        if (event.isForced()) {
            if (event.isFollowed()) {
                EmailNotification notification = new EmailNotification(event.getFollower().getUsername(), "employee_follow_forced");
                emailNotificationRepository.save(notification);
            } else {
                EmailNotification notification = new EmailNotification(event.getFollower().getUsername(), "employee_unfollow_forced");
                emailNotificationRepository.save(notification);
            }
        }
    }
}
