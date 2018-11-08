package dk.ledocsystem.ledoc.dto.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import dk.ledocsystem.ledoc.annotations.validation.PhoneNumber;
import dk.ledocsystem.ledoc.dto.employee.*;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class CustomerAdminDTO {

    @NotNull
    @Email
    private String username;

    @NotNull
    @Size(min = 3, max = 40)
    @OnlyAscii
    private String password;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String role = "admin";

    @NotNull
    @Size(min = 2, max = 40)
    @OnlyAscii
    private String firstName;

    @NotNull
    @Size(min = 2, max = 40)
    @OnlyAscii
    private String lastName;

    @Size(min = 3, max = 40)
    private String idNumber;

    @Size(min = 2, max = 40)
    private String initials;

    @PhoneNumber
    private String cellPhone;

    @PhoneNumber
    private String phoneNumber;

    @Size(min = 2, max = 40)
    private String title;

    private boolean canCreatePersonalLocation;

    private boolean welcomeMessage;

    private LocalDate expireOfIdCard;

    @Valid
    private EmployeeDetailsDTO details;

    @Valid
    private EmployeePersonalInfoDTO personalInfo;

    @Valid
    private EmployeeNearestRelativesDTO nearestRelative;
}
