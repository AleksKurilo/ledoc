package dk.ledocsystem.service.impl.events.event_listener;

import dk.ledocsystem.data.model.equipment.Equipment;
import dk.ledocsystem.service.api.EquipmentLogService;
import dk.ledocsystem.service.impl.events.event.EntityEvents;
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
