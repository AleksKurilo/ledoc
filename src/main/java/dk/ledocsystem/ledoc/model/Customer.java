package dk.ledocsystem.ledoc.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import dk.ledocsystem.ledoc.dto.customer.CustomerCreateDTO;
import dk.ledocsystem.ledoc.dto.customer.CustomerEditDTO;
import dk.ledocsystem.ledoc.model.employee.Employee;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
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

    public Customer(@NonNull CustomerCreateDTO customerCreateDTO) {
        setName(customerCreateDTO.getName());
        setCvr(customerCreateDTO.getCvr());
        setContactPhone(customerCreateDTO.getContactPhone());
        setContactEmail(customerCreateDTO.getContactEmail());
        setInvoiceEmail(customerCreateDTO.getInvoiceEmail());
        setCompanyEmail(customerCreateDTO.getCompanyEmail());
        setMailbox(customerCreateDTO.getMailbox());
    }

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_seq")
    @SequenceGenerator(name = "customer_seq", sequenceName = "customer_seq")
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String name; //ok

    @Column(nullable = false, length = 40, unique = true)
    private String cvr; //ok

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_of_contact")
    @JsonSerialize(as = NamedEntity.class)
    private Employee pointOfContact; //ok, only superadmin

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "trade_to_customer",
            joinColumns = {@JoinColumn(name = "customer_id")},
            inverseJoinColumns = {@JoinColumn(name = "trade_id")})
    private Set<Trade> trades = new HashSet<>(); //ok

    @Column(name = "contact_phone", length = 20)
    private String contactPhone; //ok

    @Column(name = "contact_email")
    private String contactEmail; //ok

    @Column(name = "invoice_email")
    private String invoiceEmail; //ok

    @Column(name = "company_email")
    private String companyEmail; //ok

    @Column
    private String mailbox;

    @ColumnDefault("false")
    @Column(nullable = false)
    private Boolean archived;

    public void updateProperties(@NonNull CustomerEditDTO customerEditDTO) {
        setName(customerEditDTO.getName() != null ? customerEditDTO.getName() : getName());
        setCvr(customerEditDTO.getCvr() != null ? customerEditDTO.getCvr() : getCvr());
        setContactPhone(customerEditDTO.getContactPhone() != null ? customerEditDTO.getContactPhone() : getContactPhone());
        setContactEmail(customerEditDTO.getContactEmail() != null ? customerEditDTO.getContactEmail(): getContactEmail());
        setInvoiceEmail(customerEditDTO.getInvoiceEmail() != null ? customerEditDTO.getInvoiceEmail() : getInvoiceEmail());
        setCompanyEmail(customerEditDTO.getCompanyEmail() != null ? customerEditDTO.getCompanyEmail() : getCompanyEmail());
        setMailbox(customerEditDTO.getMailbox() != null ? customerEditDTO.getMailbox() : getMailbox());
        setArchived(customerEditDTO.getArchived() != null ? customerEditDTO.getArchived(): getArchived());
    }
}
