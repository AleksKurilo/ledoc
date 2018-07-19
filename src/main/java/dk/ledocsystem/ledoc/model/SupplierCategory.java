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
@Table(name = "supplier_categories")
@ToString(of = {"name"})
public class SupplierCategory {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "supplier_cat_seq")
    @SequenceGenerator(name = "supplier_cat_seq", sequenceName = "supplier_cat_seq")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description;
}
