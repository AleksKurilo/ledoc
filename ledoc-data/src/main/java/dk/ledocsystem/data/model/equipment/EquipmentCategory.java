package dk.ledocsystem.data.model.equipment;

import dk.ledocsystem.data.model.DoubleNamed;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.Period;

@Setter
@Getter
@Entity
@Table(name = "equipment_categories")
@ToString(of = "nameEn")
public class EquipmentCategory implements DoubleNamed {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "equipment_cat_seq")
    @SequenceGenerator(name = "equipment_cat_seq", sequenceName = "equipment_cat_seq")
    private Long id;

    @Column(name = "name_en", nullable = false, unique = true)
    private String nameEn;

    @Column(name = "name_da", nullable = false, unique = true)
    private String nameDa;

    @Column(name = "review_frequency")
    private Period reviewFrequency;
}
