package dk.ledocsystem.ledoc.model;

import dk.ledocsystem.ledoc.model.employee.Employee;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "equipment", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "customer_id"})})
@ToString(of = {"id", "name"})
@DynamicInsert
@DynamicUpdate
public class Equipment implements Visitable {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "equipment_seq")
    @SequenceGenerator(name = "equipment_seq", sequenceName = "equipment_seq")
    private Long id;

    @Column(nullable = false, length = 40)
    private String name;

    @Column(name = "id_number", length = 40)
    private String idNumber;

    @Column(name = "serial_number", length = 40)
    private String serialNumber;

    @Column(name = "local_id", length = 40)
    private String localId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private EquipmentCategory category;

    @Column(length = 40)
    private String manufacturer;

    @Column
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    @ColumnDefault("OK")
    private Status status;

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @Column(name = "warranty_date")
    private LocalDate warrantyDate;

    @ColumnDefault("false")
    @Column(nullable = false)
    private Boolean archived;

    @Column(name = "archive_reason")
    private String archiveReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private Employee creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_id", nullable = false)
    private Employee responsible;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Customer customer;

    @Column(length = 400)
    private String remark;

    @ManyToMany
    @JoinTable(name = "equipment_log",
            joinColumns = { @JoinColumn(name = "equipment_id")},
            inverseJoinColumns = { @JoinColumn(name = "employee_id") })
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Employee> visitedBy;
}
