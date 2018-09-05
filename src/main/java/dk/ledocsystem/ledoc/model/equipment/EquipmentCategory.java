package dk.ledocsystem.ledoc.model.equipment;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "equipment_categories")
@ToString(of = "name")
public class EquipmentCategory {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "equipment_cat_seq")
    @SequenceGenerator(name = "equipment_cat_seq", sequenceName = "equipment_cat_seq")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description;
}
