package dk.ledocsystem.ledoc.dto.employee;

import com.fasterxml.jackson.annotation.JsonAlias;
import dk.ledocsystem.ledoc.annotations.validation.MobilePhone;
import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import dk.ledocsystem.ledoc.annotations.validation.employee.UniqueUsername;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class EmployeeEditDTO {

    @Email
    @UniqueUsername
    @OnlyAscii
    private String username;

    @Size(min = 3, max = 40)
    @OnlyAscii
    private String password;

    @Size(min = 2, max = 40)
    @OnlyAscii
    private String firstName;

    @Size(min = 2, max = 40)
    @OnlyAscii
    private String lastName;

    @Size(min =  3, max = 40)
    private String idNumber;

    @Size(min = 2, max = 40)
    private String initials;

    @MobilePhone
    private String cellPhone;

    @MobilePhone
    private String phoneNumber;

    private Boolean canCreatePersonalLocation;

    private LocalDate expireOfIdCard;

    @Valid
    @JsonAlias("details")
    private EmployeeDetailsEditDTO employeeDetailsEditDTO;

    @Valid
    @JsonAlias("personalInfo")
    private EmployeePersonalInfoDTO employeePersonalInfoDTO;

    @Valid
    @JsonAlias("nearestRelatives")
    private EmployeeNearestRelativesDTO employeeNearestRelativesDTO;
}
