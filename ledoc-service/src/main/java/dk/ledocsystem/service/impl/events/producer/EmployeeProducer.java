package dk.ledocsystem.service.impl.events.producer;

import com.google.common.collect.ImmutableMap;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.logging.LogType;
import dk.ledocsystem.service.api.dto.inbound.employee.EmployeeCreateDTO;
import dk.ledocsystem.service.impl.events.event.EditEvent;
import dk.ledocsystem.service.impl.events.event.EntityEvents;
import dk.ledocsystem.service.impl.events.event.NotificationEvents;
import dk.ledocsystem.service.impl.utils.diff.DiffFinder;
import dk.ledocsystem.service.impl.utils.diff.SingleDiff;
import dk.ledocsystem.service.impl.utils.diff.comparators.EmployeeComparator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmployeeProducer {
    private final ApplicationEventPublisher publisher;
    private final DiffFinder diffFinder;
    private final ModelMapper modelMapper;

    public void create(EmployeeCreateDTO employeeCreateDTO, Employee loggedInEmployee) {
        Employee employee = modelMapper.map(employeeCreateDTO, Employee.class);
        publisher.publishEvent(new EntityEvents<>(employee, loggedInEmployee, LogType.Create));

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

    public void read(Employee employee, Employee loggedInEmployee, boolean saveLog) {
        publisher.publishEvent(new EntityEvents<>(employee, loggedInEmployee, LogType.Read, saveLog));
    }

    public void edit(Employee employeeBeforeEdit, Employee employeeAfterEdit, Employee loggedInEmployee) {
        List<SingleDiff> diffList = diffFinder.findDiff(employeeBeforeEdit, employeeAfterEdit, EmployeeComparator.INSTANCE);
        publisher.publishEvent(new EditEvent<>(employeeAfterEdit, loggedInEmployee, diffList));
        if (employeeAfterEdit.getResponsible() != null && !employeeAfterEdit.getResponsible().equals(employeeBeforeEdit.getResponsible())) {
            publisher.publishEvent(new NotificationEvents(employeeAfterEdit.getResponsible().getUsername(),
                    "employee_responsible_changed", ImmutableMap.of("employeeName", employeeAfterEdit.getName())));
        }
    }

    public void review(Employee employee, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents<>(employee, loggedInEmployee, LogType.Review));
    }

    public void archive(Employee employee, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents<>(employee, loggedInEmployee, LogType.Archive));
    }

    public void unarchive(Employee employee, Employee loggedInEmployee) {
        publisher.publishEvent(new EntityEvents<>(employee, loggedInEmployee, LogType.Unarchive));
    }

    public void follow(Employee employee, Employee follower, boolean forced, boolean followed) {
        if (forced) {
            Map<String, Object> model = ImmutableMap.of("employee", employee.getUsername());
            if (followed) {
                publisher.publishEvent(new NotificationEvents(follower.getUsername(), "employee_follow_forced", model));
            } else {
                publisher.publishEvent(new NotificationEvents(follower.getUsername(), "employee_unfollow_forced", model));
            }
        }
    }
}
