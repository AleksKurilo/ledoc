package dk.ledocsystem.data.model.supplier;

import dk.ledocsystem.data.model.DoubleNamed;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "supplier_categories")
@ToString(of = "nameEn")
public class SupplierCategory implements DoubleNamed {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "supplier_cat_seq")
    @SequenceGenerator(name = "supplier_cat_seq", sequenceName = "supplier_cat_seq")
    private Long id;

    @Column(name = "name_en", nullable = false, unique = true)
    private String nameEn;

    @Column(name = "name_da", nullable = false, unique = true)
    private String nameDa;

    @Column
    private String description;
}
