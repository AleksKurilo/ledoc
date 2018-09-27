package dk.ledocsystem.ledoc.model.employee;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import dk.ledocsystem.ledoc.config.security.UserAuthorities;
import dk.ledocsystem.ledoc.model.*;
import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Table;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "employees")
@ToString(of = {"username", "firstName", "lastName"})
@DynamicInsert
@DynamicUpdate
public class Employee implements Visitable, NamedEntity {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_seq")
    @SequenceGenerator(name = "employee_seq", sequenceName = "employee_seq")
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @JsonIgnore
    @Column(nullable = false, length = 56)
    private String password;

    @Column(name = "id_number", length = 40)
    private String idNumber;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(length = 40)
    private String initials;

    @Column(name = "cell_phone", length = 40)
    private String cellPhone;

    @Column(name = "phone_number", length = 40)
    private String phoneNumber;

    @Column(length = 40)
    private String title;

    @ElementCollection
    @CollectionTable(name = "employee_authorities")
    @Column(name = "authority", nullable = false)
    private Set<UserAuthorities> authorities;

    @ManyToMany
    @JoinTable(name = "employee_log",
            joinColumns = { @JoinColumn(name = "visited_id")},
            inverseJoinColumns = { @JoinColumn(name = "employee_id",
                    foreignKey = @ForeignKey(foreignKeyDefinition = "foreign key (employee_id) references employees on delete cascade")) })
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Employee> visitedBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_id")
    private Employee responsible;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "place_of_employment_id")
    private Location placeOfEmployment;

    @Column(name = "expire_id_card")
    private LocalDate expireOfIdCard;

    @Embedded
    private EmployeeDetails details = new EmployeeDetails();

    @Embedded
    private EmployeePersonalInfo personalInfo = new EmployeePersonalInfo();

    @Embedded
    private EmployeeNearestRelative nearestRelative = new EmployeeNearestRelative();

    @Embedded
    @JsonUnwrapped
    private Avatar avatar = new Avatar();

    @ManyToMany(mappedBy = "employees")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Location> locations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Customer customer;

    @ColumnDefault("false")
    @Column(nullable = false)
    private Boolean archived;

    @Column(name = "archive_reason")
    private String archiveReason;

    @Override
    public String getName() {
        return firstName + " " + lastName;
    }
}
