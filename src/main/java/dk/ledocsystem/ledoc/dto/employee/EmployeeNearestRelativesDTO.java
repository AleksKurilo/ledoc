package dk.ledocsystem.ledoc.dto.employee;

import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import dk.ledocsystem.ledoc.annotations.validation.employee.PhoneNumber;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
public class EmployeeNearestRelativesDTO {

    @OnlyAscii
    @Size(min = 2, max = 40)
    private String firstName;

    @OnlyAscii
    @Size(min = 2, max = 40)
    private String lastName;

    @OnlyAscii
    @Size(min = 3, max = 400)
    private String comment;

    @Email
    private String email;

    @PhoneNumber
    private String phoneNumber;
}
