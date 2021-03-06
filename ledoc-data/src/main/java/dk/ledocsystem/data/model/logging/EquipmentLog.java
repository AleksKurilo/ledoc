package dk.ledocsystem.data.model.logging;

import dk.ledocsystem.data.model.equipment.Equipment;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;


@Getter
@Setter
@Entity(name="EquipmentLog")
@Table(name="equipment_logs")
public class EquipmentLog extends AbstractLog {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;
}
