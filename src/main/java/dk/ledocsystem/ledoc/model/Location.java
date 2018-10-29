package dk.ledocsystem.ledoc.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import dk.ledocsystem.ledoc.model.employee.Employee;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;
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

    @ColumnDefault("CURRENT_DATE")
    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_id")
    private Employee responsible;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private Employee createdBy;

    @OneToOne(mappedBy = "location", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private Address address;

    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    @JsonIdentityReference(alwaysAsId = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "address_location_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Location addressLocation;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "addressLocation")
    @Fetch(FetchMode.SUBSELECT)
    private Set<Location> physicalLocations;

    @ManyToMany(mappedBy = "locations")
    @OnDelete(action = OnDeleteAction.CASCADE)
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

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    public LocationType type;

    public void setAddress(@NonNull Address address) {
        this.address = address;
        address.setLocation(this);
    }

    public void removeAddress() {
        if (this.address != null) {
            this.address.setLocation(null);
        }
        this.address = null;
    }
}
