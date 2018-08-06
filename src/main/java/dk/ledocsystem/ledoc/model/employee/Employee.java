package dk.ledocsystem.ledoc.model.employee;

import dk.ledocsystem.ledoc.config.security.UserAuthorities;
import dk.ledocsystem.ledoc.dto.EmployeeDTO;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.Location;
import lombok.*;
import org.hibernate.annotations.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "employees")
@ToString(of = {"username", "firstName", "lastName"})
@DynamicInsert
@DynamicUpdate
public class Employee {

    public Employee(EmployeeDTO employeeDTO) {
        setUsername(employeeDTO.getUsername());
        setPassword(employeeDTO.getPassword());
        setIdNumber(employeeDTO.getIdNumber());
        setFirstName(employeeDTO.getFirstName());
        setLastName(employeeDTO.getLastName());
        setInitials(employeeDTO.getInitials());
        setCellPhone(employeeDTO.getCellPhone());
        setPhoneNumber(employeeDTO.getPhoneNumber());
        setCanCreatePersonalLocation(employeeDTO.isCanCreatePersonalLocation());
        setExpireOfIdCard(employeeDTO.getExpireOfIdCard());
        setDetails(new EmployeeDetails(employeeDTO.getEmployeeDetailsDTO()));
        setPersonalInfo(new EmployeePersonalInfo(employeeDTO.getEmployeePersonalInfoDTO()));
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

    //autogenerate
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
    private List<UserAuthorities> authorities;

    @OneToOne
    @JoinColumn(name = "responsible_id")
    private Employee responsible;

    private boolean welcomeMessage;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "place_of_employment_id")
    private Location placeOfEmployment;

    @ColumnDefault("false")
    @Column(name = "create_pers_location")
    private Boolean canCreatePersonalLocation;

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

    public void updateProperties(EmployeeDTO employeeDTO) {
        setUsername(employeeDTO.getUsername());
        setPassword(employeeDTO.getPassword());
        setIdNumber(employeeDTO.getIdNumber());
        setFirstName(employeeDTO.getFirstName());
        setLastName(employeeDTO.getLastName());
        setInitials(employeeDTO.getInitials());
        setCellPhone(employeeDTO.getCellPhone());
        setPhoneNumber(employeeDTO.getPhoneNumber());
        setCanCreatePersonalLocation(employeeDTO.isCanCreatePersonalLocation());
        setExpireOfIdCard(employeeDTO.getExpireOfIdCard());
        setDetails(new EmployeeDetails(employeeDTO.getEmployeeDetailsDTO()));
        setPersonalInfo(new EmployeePersonalInfo(employeeDTO.getEmployeePersonalInfoDTO()));
    }
}