package dk.ledocsystem.ledoc.events.event_listener;

import dk.ledocsystem.ledoc.events.event.EntityEvents;
import dk.ledocsystem.ledoc.model.equipment.Equipment;
import dk.ledocsystem.ledoc.service.EquipmentLogService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EquipmentEventListener {
    private final EquipmentLogService equipmentLogService;
    @EventListener(condition = "#event.saveLog")
    public void onApplicationEvent(EntityEvents<Equipment> event) {
        equipmentLogService.createLog(event.getLoggedInEmployee(), event.getSource(), event.getLogType());
    }
}
