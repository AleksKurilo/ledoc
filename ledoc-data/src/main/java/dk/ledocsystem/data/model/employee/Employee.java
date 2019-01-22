package dk.ledocsystem.data.model.employee;

import dk.ledocsystem.data.model.Customer;
import dk.ledocsystem.data.model.Location;
import dk.ledocsystem.data.model.document.FollowedDocument;
import dk.ledocsystem.data.model.equipment.FollowedEquipment;
import dk.ledocsystem.data.model.logging.EmployeeLog;
import dk.ledocsystem.data.model.security.UserAuthorities;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "employees")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@DynamicInsert
@DynamicUpdate
public class Employee {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_seq")
    @SequenceGenerator(name = "employee_seq", sequenceName = "employee_seq")
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

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

    @Column(name = "cell_phone", length = 25)
    private String cellPhone;

    @Column(name = "phone_number", length = 25)
    private String phoneNumber;

    @Column(length = 40)
    private String title;

    @ColumnDefault("false")
    @Column(nullable = false)
    private Boolean archived;

    @Column(name = "archive_reason")
    private String archiveReason;

    @Column(name = "expire_id_card")
    private LocalDate expireOfIdCard;

    @Column(name = "avatar")
    private String avatar;

    @Embedded
    private EmployeeDetails details = new EmployeeDetails();

    @Embedded
    private EmployeePersonalInfo personalInfo = new EmployeePersonalInfo();

    @Embedded
    private EmployeeNearestRelative nearestRelative = new EmployeeNearestRelative();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_id")
    private Employee responsible;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "place_of_employment_id")
    private Location placeOfEmployment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Customer customer;

    @Fetch(FetchMode.SUBSELECT)
    @ElementCollection
    @CollectionTable(name = "employee_authorities")
    @Column(name = "authority", nullable = false)
    private Set<UserAuthorities> authorities = new HashSet<>();

    @OneToMany(mappedBy = "targetEmployee")
    @Where(clause = "type = 'Read' OR type = 'Archive'")
    private Set<EmployeeLog> visitedLogs;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "employee_location",
            joinColumns = {@JoinColumn(name = "employee_id")},
            inverseJoinColumns = {@JoinColumn(name = "location_id")})
    private Set<Location> locations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Employee creator;

    @OneToMany(mappedBy = "employee", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private Set<FollowedEmployees> followedEmployees;

    @OneToMany(mappedBy = "employee")
    private Set<FollowedEquipment> followedEquipments;

    @OneToMany(mappedBy = "employee")
    private Set<FollowedDocument> followedDocuments;

    public String getName() {
        return firstName + " " + lastName;
    }

    public UserAuthorities getRole() {
        return (authorities.contains(UserAuthorities.ADMIN)) ? UserAuthorities.ADMIN : UserAuthorities.USER;
    }

    public void addFollower(Employee employee, boolean forced) {
        FollowedEmployees followedEmployee = new FollowedEmployees(employee, this, forced);
        followedEmployees.add(followedEmployee);
        employee.getFollowedEmployees().add(followedEmployee);
    }

    public void removeFollower(Employee employee) {
        for (Iterator<FollowedEmployees> iterator = followedEmployees.iterator();
             iterator.hasNext(); ) {
            FollowedEmployees followedEmployee = iterator.next();

            if (followedEmployee.getFollowed().equals(this) &&
                    followedEmployee.getEmployee().equals(employee)) {
                iterator.remove();
                followedEmployee.getEmployee().getFollowedEmployees().remove(followedEmployee);
            }
        }
    }

    @Override
    public String toString() {
        return getName();
    }
}
