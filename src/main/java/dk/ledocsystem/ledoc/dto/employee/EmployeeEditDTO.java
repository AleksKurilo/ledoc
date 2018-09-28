package dk.ledocsystem.ledoc.dto.employee;

import dk.ledocsystem.ledoc.annotations.validation.PhoneNumber;
import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import dk.ledocsystem.ledoc.annotations.validation.employee.UniqueUsername;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
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

    @PhoneNumber
    private String cellPhone;

    @PhoneNumber
    private String phoneNumber;

    @Size(min = 2, max = 40)
    private String title;

    private Long responsibleId;

    private Boolean canCreatePersonalLocation;

    private LocalDate expireOfIdCard;

    private String avatar;

    @Valid
    private EmployeeDetailsEditDTO details;

    @Valid
    private EmployeePersonalInfoDTO personalInfo;

    @Valid
    private EmployeeNearestRelativesDTO nearestRelative;

    private Boolean archived;
}
