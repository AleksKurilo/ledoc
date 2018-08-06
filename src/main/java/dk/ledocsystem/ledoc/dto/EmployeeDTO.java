package dk.ledocsystem.ledoc.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import dk.ledocsystem.ledoc.annotations.validation.MobilePhone;
import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import dk.ledocsystem.ledoc.annotations.validation.employee.UniqueUsername;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class EmployeeDTO {

    @NotNull(message = "Username must not be null")
    @Email
    @UniqueUsername
    @OnlyAscii(message = "Username must contain only ASCII characters")
    private String username;

    @NotNull(message = "Password must not be null")
    @Size(min = 3, max = 40, message = "Password must be at least {min} and at most {max} characters long")
    @OnlyAscii(message = "Password must contain only ASCII characters")
    private String password;

    @NotNull(message = "First name must not be null")
    @Size(min = 2, max = 40, message = "First name must be at least {min} and at most {max} characters long")
    @OnlyAscii(message = "First name must contain only ASCII characters")
    @JsonAlias("first_name")
    private String firstName;

    @NotNull(message = "Last name must not be null")
    @Size(min = 2, max = 40, message = "Last name must be at least {min} and at most {max} characters long")
    @OnlyAscii(message = "Last name must contain only ASCII characters")
    @JsonAlias("last_name")
    private String lastName;

    @Size(min =  3, max = 40, message = "Id number can not be more than 40 symbols")
    private String idNumber;

    @Size(min = 2, max = 40, message = "Initials can not be more than 40 symbols")
    private String initials;

    @MobilePhone
    private String cellPhone;

    @MobilePhone
    //@JsonAlias("mobile_phone")
    private String phoneNumber;

    private boolean welcomeMEssage;

    private boolean canCreatePersonalLocation;

    private LocalDate expireOfIdCard;

    @Valid
    private EmployeeDetailsDTO employeeDetailsDTO;

    @Valid
    private EmployeePersonalInfoDTO employeePersonalInfoDTO;

    @Valid
    private EmployeeNearestRelativesDTO employeeNearestRelativesDTO;
}
