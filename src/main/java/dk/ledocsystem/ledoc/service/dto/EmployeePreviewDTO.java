package dk.ledocsystem.ledoc.service.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class EmployeePreviewDTO {

    private String username;

    private String role;

    private String firstName;

    private String lastName;

    private String idNumber;

    private String initials;

    private String cellPhone;

    private String phoneNumber;

    private String title;

    private String responsibleName;

    private Set<String> locationNames;

    private boolean canCreatePersonalLocation;

    private LocalDate expireOfIdCard;

    private String avatar;

    private EmployeeDetailsPreviewDTO details = new EmployeeDetailsPreviewDTO();

    private EmployeePersonalInfoDTO personalInfo = new EmployeePersonalInfoDTO();

    private EmployeeNearestRelativesDTO nearestRelative = new EmployeeNearestRelativesDTO();
}
