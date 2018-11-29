package dk.ledocsystem.service.impl.events.producer;

import com.google.common.collect.ImmutableMap;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.logging.LogType;
import dk.ledocsystem.service.api.dto.inbound.employee.EmployeeCreateDTO;
import dk.ledocsystem.service.impl.events.event.EntityEvents;
import dk.ledocsystem.service.impl.events.event.NotificationEvents;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmployeeProducer {
    private final ApplicationEventPublisher publisher;

    private final ModelMapper modelMapper;

    public void create(EmployeeCreateDTO employeeCreateDTO, Employee loggedInEmployee) {
        Employee employee = modelMapper.map(employeeCreateDTO, Employee.class);
        publisher.publishEvent(new EntityEvents(employee, loggedInEmployee, LogType.Create));

        if (employeeCreateDTO.isWelcomeMessage()) {
            Map<String, Object> model = ImmutableMap.<String, Object>builder()
                    .put("username", employeeCreateDTO.getUsername())
                    .put("password", employeeCreateDTO.getPassword())
                    .build();
            publisher.publishEvent(new NotificationEvents(employeeCreateDTO.getUsername(), "welcome", model));
        }

        if (employee.getResponsible() != null) {
            publisher.publishEvent(new NotificationEvents(employee.getResponsible().getUsername(), "employee_created"));
        }
    }

    public void read(Employee employee, Employee loggedInEmployee, final boolean saveLog) {
        publisher.publishEvent(new EntityEvents(employee, loggedInEmployee, LogType.Read, saveLog));
    }

    public void edit(Employee employee, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents(employee, loggedInEmployee, LogType.Edit));
        if (employee.getResponsible() != null) {
            publisher.publishEvent(new NotificationEvents(employee.getResponsible().getUsername(), "employee_edited"));
        }
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
        if (forced) {
            Map<String, Object> model = ImmutableMap.<String, Object>builder()
                    .put("employee", employee.getUsername())
                    .build();
            if (followed) {
                publisher.publishEvent(new NotificationEvents(follower.getUsername(), "employee_follow_forced", model));
            } else {
                publisher.publishEvent(new NotificationEvents(follower.getUsername(), "employee_unfollow_forced", model));
            }
        }
    }
}
