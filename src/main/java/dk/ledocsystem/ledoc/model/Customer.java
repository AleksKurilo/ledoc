package dk.ledocsystem.ledoc.model;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Setter
@Getter
@Entity
@Table(name = "customers")
@ToString(of = {"name", "contactPhone", "contactEmail"})
public class Customer {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "customer_seq")
    @SequenceGenerator(name = "customer_seq", sequenceName = "customer_seq")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, length = 20, unique = true)
    private String cvr;

    @Column(name = "contact_phone", length = 50)
    private String contactPhone;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "invoice_email")
    private String invoiceEmail;

    @Column(name = "company_email")
    private String companyEmail;

    @ColumnDefault("false")
    @Column(nullable = false)
    private Boolean archived;
}
