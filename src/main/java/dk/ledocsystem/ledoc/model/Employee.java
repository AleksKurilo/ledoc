package dk.ledocsystem.ledoc.model;

import dk.ledocsystem.ledoc.dto.EmployeeDTO;
import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

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
        setMobilePhone(employeeDTO.getMobilePhone());
        setTitle(employeeDTO.getTitle());
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

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "mobile_phone", length = 50)
    private String mobilePhone;

    @Column(nullable = false)
    private String title;

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
        setMobilePhone(employeeDTO.getMobilePhone());
        setTitle(employeeDTO.getTitle());
    }
}
