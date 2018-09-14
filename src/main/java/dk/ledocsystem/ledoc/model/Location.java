package dk.ledocsystem.ledoc.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "locations", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "customer_id"})})
@ToString(of = {"id", "name"})
@DynamicInsert
@DynamicUpdate
public class Location {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "location_seq")
    @SequenceGenerator(name = "location_seq", sequenceName = "location_seq")
    private Long id;

    @Column(nullable = false, length = 40)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Employee responsible;

    @OneToOne(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Address address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_location_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Location addressLocation;

    @ManyToMany
    @JoinTable(name = "employee_location",
            joinColumns = { @JoinColumn(name = "location_id")},
            inverseJoinColumns = { @JoinColumn(name = "employee_id")})
    private Set<Employee> employees;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Customer customer;

    @Column(name = "is_cust_first", nullable = false)
    @ColumnDefault("false")
    private Boolean isCustomerFirst;

    @ColumnDefault("false")
    @Column(nullable = false)
    private Boolean archived;

    @Column(name = "archive_reason")
    private String archiveReason;
}

