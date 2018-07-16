package dk.ledocsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Setter
@Getter

@Entity
@Table(name = "customers")
@ToString(of = {"name", "contactPhone", "contactEmail"})
public class Customer implements Serializable {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //@SequenceGenerator(name = "customer_seq", sequenceName = "customer_seq")
    private Long id;

    @EqualsAndHashCode.Include
    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 20)
    private String cvr;

    @Column(name = "contact_phone", length = 50)
    private String contactPhone;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "invoice_email")
    private String invoiceEmail;

    @Column(name = "company_email")
    private String companyEmail;

    @Column(name = "archived")
    private Boolean archived;

    @OneToMany(mappedBy = "id", fetch = FetchType.EAGER, targetEntity = Employee.class)
    private Set<Employee> employees;
/*
    private Set<Supplier> suppliers;

    private Set<Equipment> equipments;*/
}
