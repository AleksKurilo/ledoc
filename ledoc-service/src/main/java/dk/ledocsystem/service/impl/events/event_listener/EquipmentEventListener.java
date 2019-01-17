package dk.ledocsystem.service.impl.events.event_listener;

import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.equipment.Equipment;
import dk.ledocsystem.data.model.logging.EquipmentEditDetails;
import dk.ledocsystem.service.api.EquipmentLogService;
import dk.ledocsystem.service.impl.events.event.EditEvent;
import dk.ledocsystem.service.impl.events.event.EntityEvents;
import dk.ledocsystem.service.impl.utils.diff.SingleDiff;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EquipmentEventListener {
    private final EquipmentLogService equipmentLogService;

    @Async
    @TransactionalEventListener(condition = "#event.saveLog and #event.logType != T(dk.ledocsystem.data.model.logging.LogType).Edit")
    public void onApplicationEvent(EntityEvents<Equipment> event) {
        Equipment equipment = event.getSource();
        Employee loggedInEmployee = event.getLoggedInEmployee();
        equipmentLogService.createLog(loggedInEmployee, equipment, event.getLogType());
    }

    @Async
    @TransactionalEventListener
    public void onEditEvent(EditEvent<Equipment> event) {
        Equipment equipment = event.getSource();
        Employee loggedInEmployee = event.getLoggedInEmployee();
        equipmentLogService.createEditLog(loggedInEmployee, equipment, convertDiff(event.getDiffList()));
    }

    private List<EquipmentEditDetails> convertDiff(List<SingleDiff> diffList) {
        return diffList.stream()
                .map(diff -> new EquipmentEditDetails(diff.getProperty(), diff.getPreviousValue(), diff.getCurrentValue()))
                .collect(Collectors.toList());
    }
}
