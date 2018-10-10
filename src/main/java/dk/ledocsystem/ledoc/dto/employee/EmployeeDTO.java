package dk.ledocsystem.ledoc.dto.employee;


import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import dk.ledocsystem.ledoc.annotations.validation.PhoneNumber;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Data
public class EmployeeDTO {

    @NotNull
    @Email
    @OnlyAscii
    private String username;

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
