package dk.ledocsystem.ledoc.model;

import dk.ledocsystem.ledoc.dto.CustomerDTO;
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

    public Customer(CustomerDTO customerDTO) {
        setName(customerDTO.getName());
        setCvr(customerDTO.getCvr());
        setContactPhone(customerDTO.getContactPhone());
        setContactEmail(customerDTO.getContactEmail());
        setInvoiceEmail(customerDTO.getInvoiceEmail());
        setCompanyEmail(customerDTO.getCompanyEmail());
        setMailbox(customerDTO.getMailbox());
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

    @OneToOne
    @JoinColumn(name = "point_of_contact")
    private Employee pointOfContact; //ok, only superadmin

    @ManyToMany(cascade = CascadeType.ALL)
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

    public void updateProperties(CustomerDTO customerDTO) {
        setName(customerDTO.getName());
        setCvr(customerDTO.getCvr());
        setContactPhone(customerDTO.getContactPhone());
        setContactEmail(customerDTO.getContactEmail());
        setInvoiceEmail(customerDTO.getInvoiceEmail());
        setCompanyEmail(customerDTO.getCompanyEmail());
        setMailbox(customerDTO.getMailbox());
    }
}
