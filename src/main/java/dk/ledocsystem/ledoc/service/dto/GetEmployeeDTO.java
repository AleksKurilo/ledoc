package dk.ledocsystem.ledoc.service.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class GetEmployeeDTO {

    private String username;

    private String role;

    private String firstName;

    private String lastName;

    private String idNumber;

    private String initials;

    private String cellPhone;

    private String phoneNumber;

    private String title;

    private Long responsibleId;

    private Set<Long> locationIds;

    private boolean canCreatePersonalLocation;

    private LocalDate expireOfIdCard;

    private String avatar;

    private EmployeeDetailsDTO details;

    private EmployeePersonalInfoDTO personalInfo;

    private EmployeeNearestRelativesDTO nearestRelative;
}
