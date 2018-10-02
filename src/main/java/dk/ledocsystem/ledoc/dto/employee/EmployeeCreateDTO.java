package dk.ledocsystem.ledoc.dto.employee;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import dk.ledocsystem.ledoc.annotations.validation.PhoneNumber;
import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import dk.ledocsystem.ledoc.annotations.validation.employee.UniqueUsername;
import dk.ledocsystem.ledoc.model.Avatar;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Data
public class EmployeeCreateDTO {

    @NotNull
    @Email
    @UniqueUsername
    @OnlyAscii
    private String username;

    @NotNull
    @Size(min = 3, max = 40)
    @OnlyAscii
    private String password;

    @Pattern(regexp = "admin|user", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String role;

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

    private Long responsibleId;

    @NotEmpty
    private Set<Long> locationIds;

    private boolean welcomeMessage;

    private boolean canCreatePersonalLocation;

    private LocalDate expireOfIdCard;

    @JsonUnwrapped
    private Avatar avatar;

    @Valid
    private EmployeeDetailsCreateDTO details;

    @Valid
    private EmployeePersonalInfoDTO personalInfo;

    @Valid
    private EmployeeNearestRelativesDTO nearestRelative;
}
