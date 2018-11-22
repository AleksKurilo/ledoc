package dk.ledocsystem.data.model;

import dk.ledocsystem.data.model.employee.Employee;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "customers")
@ToString(of = {"name", "contactPhone", "contactEmail"})
@DynamicInsert
@DynamicUpdate
public class Customer {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_seq")
    @SequenceGenerator(name = "customer_seq", sequenceName = "customer_seq")
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String name;

    @Column(nullable = false, length = 40, unique = true)
    private String cvr;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_of_contact", nullable = false)
    private Employee pointOfContact;

    @ManyToMany
    @JoinTable(name = "trade_to_customer",
            joinColumns = {@JoinColumn(name = "customer_id")},
            inverseJoinColumns = {@JoinColumn(name = "trade_id")})
    private Set<Trade> trades = new HashSet<>();

    @Column(name = "contact_phone", length = 25)
    private String contactPhone;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "invoice_email")
    private String invoiceEmail;

    @Column(name = "company_email")
    private String companyEmail;

    @Column
    private String mailbox;

    @ColumnDefault("false")
    @Column(nullable = false)
    private Boolean archived;

    @ColumnDefault("CURRENT_DATE")
    @Column(name = "date_of_creation", nullable = false)
    private LocalDate dateOfCreation;
}
