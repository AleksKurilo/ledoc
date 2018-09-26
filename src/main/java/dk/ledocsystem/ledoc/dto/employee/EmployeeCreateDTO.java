package dk.ledocsystem.ledoc.dto.employee;

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

    @NotNull
    @Size(min = 2, max = 40)
    @OnlyAscii
    private String firstName;

    @NotNull
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

    @Size(min = 2, max = 40)
    private String title;

    private Long responsibleId;

    private boolean welcomeMessage;

    private boolean canCreatePersonalLocation;

    private LocalDate expireOfIdCard;

    private String avatar;

    @Valid
    @NotNull
    private EmployeeDetailsCreateDTO details;

    @Valid
    private EmployeePersonalInfoDTO personalInfo;

    @Valid
    private EmployeeNearestRelativesDTO nearestRelative;
}
