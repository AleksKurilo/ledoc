package dk.ledocsystem.service.impl.events.event_listener;

import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.equipment.Equipment;
import dk.ledocsystem.service.api.EquipmentLogService;
import dk.ledocsystem.service.impl.events.event.EntityEvents;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class EquipmentEventListener {
    private final EquipmentLogService equipmentLogService;

    @Async
    @TransactionalEventListener(condition = "#event.saveLog")
    public void onApplicationEvent(EntityEvents<Equipment> event) {
        Equipment equipment = event.getSource();
        Employee loggedInEmployee = event.getLoggedInEmployee();
        equipmentLogService.createLog(loggedInEmployee, equipment, event.getLogType());
    }
}
