package dk.ledocsystem.ledoc.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Set;

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

    @Column(unique = true)
    private String name;

    @Column
    private String description;

    @OneToMany(mappedBy = "id", fetch = FetchType.LAZY, targetEntity = Equipment.class)
    private Set<Equipment> equipments;
}
