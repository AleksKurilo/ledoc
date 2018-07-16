package dk.ledocsystem.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Setter
@Getter

@Entity
@Table(name = "employees")
@ToString(of = {"username", "firstName", "lastName"})
public class Employee implements Serializable {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @EqualsAndHashCode.Include
    @Column
    private String username;

    @Column
    private String password;

    @Column(name = "password_salt")
    private String passwordSalt;

    @Column
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column
    private String role;

    @Column(name = "mobile_phone")
    private String mobilePhone;

    @Column
    private String title;

    @JsonBackReference("employees")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column
    private Boolean archived;

    @Column(name = "archive_reason")
    private String archiveReason;

    /*@Override
    public String toString() {
        return "Employee{" +
                "username='" + username + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }*/
}
