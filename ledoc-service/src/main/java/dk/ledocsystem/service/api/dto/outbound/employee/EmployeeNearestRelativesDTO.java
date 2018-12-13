package dk.ledocsystem.service.api.dto.outbound.employee;

import lombok.Data;

@Data
public class EmployeeNearestRelativesDTO {

    private String firstName;

    private String lastName;

    private String comment;

    private String email;

    private String phoneNumber;

    private String name;
}
