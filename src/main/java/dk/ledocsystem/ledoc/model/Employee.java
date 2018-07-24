package dk.ledocsystem.ledoc.model;

import dk.ledocsystem.ledoc.dto.EmployeeDTO;
import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

@Setter
@Getter
@Entity
@Table(name = "employees")
@ToString(of = {"email", "firstName", "lastName"})
@DynamicInsert
@DynamicUpdate
public class Employee {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employee_seq")
    @SequenceGenerator(name = "employee_seq", sequenceName = "employee_seq")
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

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
        setEmail(employeeDTO.getEmail());
        setPassword(employeeDTO.getPassword());
        setFirstName(employeeDTO.getFirstName());
        setLastName(employeeDTO.getLastName());
        setMobilePhone(employeeDTO.getMobilePhone());
        setTitle(employeeDTO.getTitle());
    }

    public static Employee fromDTO(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        employee.setEmail(employeeDTO.getEmail());
        employee.setPassword(employeeDTO.getPassword());
        employee.setFirstName(employeeDTO.getFirstName());
        employee.setLastName(employeeDTO.getLastName());
        employee.setMobilePhone(employeeDTO.getMobilePhone());
        employee.setTitle(employeeDTO.getTitle());
        return employee;
    }
}
