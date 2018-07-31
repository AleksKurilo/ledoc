package dk.ledocsystem.ledoc.model.employee;

import dk.ledocsystem.ledoc.dto.EmployeeDTO;
import dk.ledocsystem.ledoc.model.Customer;
import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;

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
        setFirstName(employeeDTO.getFirstName());
        setLastName(employeeDTO.getLastName());
        getPersonalInfo().setPersonalMobile(employeeDTO.getMobilePhone());
        getDetails().setTitle(employeeDTO.getTitle());
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

    @OneToOne
    @JoinColumn(name = "responsible_id")
    private Employee responsible;

    private boolean welcomeMessage;

    @ColumnDefault("false")
    @Column(name = "create_pers_location")
    private Boolean canCreatePersonalLocation;

    @Column(name = "expire_id_card")
    private LocalDate expireOfIdCard;

    @Embedded
    private EmployeeDetails details;

    @Embedded
    private EmployeePersonalInfo personalInfo;



    @ManyToOne(fetch = FetchType.LAZY)
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
        setFirstName(employeeDTO.getFirstName());
        setLastName(employeeDTO.getLastName());
        getPersonalInfo().setPersonalMobile(employeeDTO.getMobilePhone());
        getDetails().setTitle(employeeDTO.getTitle());
    }
}
