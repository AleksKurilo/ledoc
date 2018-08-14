package dk.ledocsystem.ledoc.model.employee;

import dk.ledocsystem.ledoc.config.security.UserAuthorities;
import dk.ledocsystem.ledoc.dto.employee.EmployeeCreateDTO;
import dk.ledocsystem.ledoc.dto.employee.EmployeeEditDTO;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.NamedEntity;
import dk.ledocsystem.ledoc.model.Visitable;
import dk.ledocsystem.ledoc.model.Location;
import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Set;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "employees")
@ToString(of = {"username", "firstName", "lastName"})
@DynamicInsert
@DynamicUpdate
public class Employee implements Visitable, NamedEntity {

    public Employee(@NonNull EmployeeCreateDTO employeeCreateDTO) {
        setUsername(employeeCreateDTO.getUsername());
        setPassword(employeeCreateDTO.getPassword());
        setIdNumber(employeeCreateDTO.getIdNumber());
        setFirstName(employeeCreateDTO.getFirstName());
        setLastName(employeeCreateDTO.getLastName());
        setInitials(employeeCreateDTO.getInitials());
        setCellPhone(employeeCreateDTO.getCellPhone());
        setPhoneNumber(employeeCreateDTO.getPhoneNumber());
        setExpireOfIdCard(employeeCreateDTO.getExpireOfIdCard());
        setDetails(new EmployeeDetails(employeeCreateDTO.getEmployeeDetailsCreateDTO()));
        setPersonalInfo(new EmployeePersonalInfo(employeeCreateDTO.getEmployeePersonalInfoDTO()));
        setNearestRelative(new EmployeeNearestRelative(employeeCreateDTO.getEmployeeNearestRelativesDTO()));
    }

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

    @Column(name = "cell_phone", length = 40)
    private String cellPhone;

    @Column(name = "phone_number", length = 40)
    private String phoneNumber;

    @ElementCollection
    @CollectionTable(name = "employee_authorities")
    @Column(name = "authority", nullable = false)
    private Set<UserAuthorities> authorities;

    @ManyToMany
    @JoinTable(name = "employee_log",
            joinColumns = { @JoinColumn(name = "visited_id")},
            inverseJoinColumns = { @JoinColumn(name = "employee_id") })
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

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Customer customer;

    @ColumnDefault("false")
    @Column(nullable = false)
    private Boolean archived;

    @Column(name = "archive_reason")
    private String archiveReason;

    public void updateProperties(@NonNull EmployeeEditDTO employeeDTO) {
        setUsername(defaultIfNull(employeeDTO.getUsername(), getUsername()));
        setPassword(defaultIfNull(employeeDTO.getPassword(), getPassword()));
        setIdNumber(defaultIfNull(employeeDTO.getIdNumber(), getIdNumber()));
        setFirstName(defaultIfNull(employeeDTO.getFirstName(), getFirstName()));
        setLastName(defaultIfNull(employeeDTO.getLastName(), getLastName()));
        setInitials(defaultIfNull(employeeDTO.getInitials(), getInitials()));
        setCellPhone(defaultIfNull(employeeDTO.getCellPhone(), getCellPhone()));
        setPhoneNumber(defaultIfNull(employeeDTO.getPhoneNumber(), getPhoneNumber()));
        setExpireOfIdCard(defaultIfNull(employeeDTO.getExpireOfIdCard(), getExpireOfIdCard()));
        getDetails().updateProperties(employeeDTO.getEmployeeDetailsEditDTO());
        getPersonalInfo().updateProperties(employeeDTO.getEmployeePersonalInfoDTO());
        getNearestRelative().updateProperties(employeeDTO.getEmployeeNearestRelativesDTO());
    }

    @Override
    public String getName() {
        return firstName + " " + lastName;
    }
}
