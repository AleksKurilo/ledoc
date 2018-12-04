package dk.ledocsystem.service.api.dto.inbound.employee;


import dk.ledocsystem.service.api.validation.NonCyrillic;
import dk.ledocsystem.service.api.validation.PhoneNumber;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Data
public class EmployeeDTO {

    private Long id;

    @NotNull
    @Email
    private String username;

    @Pattern(regexp = "admin|user", flags = Pattern.Flag.CASE_INSENSITIVE)
    private String role;

    @NotNull
    @Size(min = 2, max = 40)
    @NonCyrillic
    private String firstName;

    @NotNull
    @Size(min = 2, max = 40)
    @NonCyrillic
    private String lastName;

    @Size(min = 3, max = 40)
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

    private Long responsibleId;

    @NotEmpty
    private Set<Long> locationIds;

    private boolean canCreatePersonalLocation;

    private LocalDate expireOfIdCard;

    private String avatar;

    @Valid
    private EmployeeDetailsDTO details;

    @Valid
    private EmployeePersonalInfoDTO personalInfo;

    @Valid
    private EmployeeNearestRelativesDTO nearestRelative;
}
