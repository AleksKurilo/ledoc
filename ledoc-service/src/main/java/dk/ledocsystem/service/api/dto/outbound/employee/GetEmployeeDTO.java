package dk.ledocsystem.service.api.dto.outbound.employee;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GetEmployeeDTO {

    private Long id;

    private Long customerId;

    private String username;

    private String role;

    private String firstName;

    private String lastName;

    private String name;

    private String idNumber;

    private String initials;

    private String cellPhone;

    private String phoneNumber;

    private String title;

    private String responsible;

    private boolean canCreatePersonalLocation;

    private LocalDate expireOfIdCard;

    private String avatar;

    private EmployeeDetailsDTO details = new EmployeeDetailsDTO();

    private EmployeePersonalInfoDTO personalInfo = new EmployeePersonalInfoDTO();

    private EmployeeNearestRelativesDTO nearestRelative = new EmployeeNearestRelativesDTO();
}
