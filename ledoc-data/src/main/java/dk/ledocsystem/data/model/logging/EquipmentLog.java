package dk.ledocsystem.data.model.logging;

import dk.ledocsystem.data.model.equipment.Equipment;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name="equipment_logs")
public class EquipmentLog extends AbstractLog {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @OneToMany(mappedBy = "log", fetch = FetchType.EAGER, orphanRemoval = true, cascade = CascadeType.ALL)
    private List<EquipmentEditDetails> editDetails;

    public void setEditDetails(List<EquipmentEditDetails> editDetails) {
        this.editDetails = editDetails;
        editDetails.forEach(details -> details.setLog(this));
    }
}
