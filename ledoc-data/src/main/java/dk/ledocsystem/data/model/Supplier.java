package dk.ledocsystem.data.model;

import dk.ledocsystem.data.model.employee.Employee;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "suppliers",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "customer_id"}))
@ToString(of = {"name", "contactPhone", "contactEmail"})
public class Supplier {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "supplier_seq")
    @SequenceGenerator(name = "supplier_seq", sequenceName = "supplier_seq")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "contact_email")
    private String contactEmail;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SupplierCategory category;

    @ColumnDefault("false")
    @Column(nullable = false)
    private Boolean archived;

    @Column(name = "archive_reason")
    private String archiveReason;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "responsible_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Customer customer;
}
