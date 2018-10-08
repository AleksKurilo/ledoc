package dk.ledocsystem.ledoc.service.dto;

import lombok.Data;

@Data
public class EmployeeNearestRelativesDTO {

    private String firstName;

    private String lastName;

    private String comment;

    private String email;

    private String phoneNumber;
}
