package dk.ledocsystem.data.model.logging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Getter
@Setter
@ToString
@Entity
@Table(name = "equipment_edit_details")
@NoArgsConstructor
public class EquipmentEditDetails extends AbstractEditDetails {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @Id
    @OnDelete(action = OnDeleteAction.CASCADE)
    private EquipmentLog log;

    public EquipmentEditDetails(String property, String previousValue, String currentValue) {
        super(property, previousValue, currentValue);
    }
}