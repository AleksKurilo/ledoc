package dk.ledocsystem.ledoc.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@Entity
@Table(name = "equipment", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "customer_id"})})
public class Equipment {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "equipment_seq")
    @SequenceGenerator(name = "equipment_seq", sequenceName = "equipment_seq")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(name = "id_number")
    private String idNumber;

    @Column(name = "serial_number")
    private String serialNumber;

    @Column(name = "local_id")
    private String localId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private EquipmentCategory category;

    @Column
    private String manufacturer;

    @Column
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Column
    private Boolean archived;

    @Column(name = "archive_reason")
    private String archiveReason;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "creator_id", nullable = false)
    private Employee creator;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;
}
