package dk.ledocsystem.service.api.dto.inbound.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.ledocsystem.service.api.validation.NonCyrillic;
import dk.ledocsystem.service.api.validation.Password;
import dk.ledocsystem.service.api.validation.PhoneNumber;
import dk.ledocsystem.service.api.dto.inbound.employee.*;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;
import java.time.LocalDate;

@Data
public class CustomerAdminDTO {

    @NotNull
    @Email
    private String username;

    @NotNull
    @Password
    private String password;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String role = "admin";

    @NotNull
    @Size(min = 2, max = 40)
    @NonCyrillic
    private String firstName;

    @NotNull
    @Size(min = 2, max = 40)
    @NonCyrillic
    private String lastName;

    @Size(min = 1, max = 40)
    private String idNumber;

    @Size(min = 2, max = 40)
    @NonCyrillic
    private String initials;

    @PhoneNumber
    private String cellPhone;

    @PhoneNumber
    private String phoneNumber;

    @Size(min = 2, max = 40)
    @NonCyrillic
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

    public Class<?>[] getValidationGroups() {
        return (details == null) ? new Class[] {Default.class} : details.getValidationGroups();
    }
}
