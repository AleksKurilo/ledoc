package dk.ledocsystem.ledoc.dto;

import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import dk.ledocsystem.ledoc.annotations.validation.employee.PhoneNumber;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
public class EmployeeNearestRelativesDTO {

    @OnlyAscii
    @Size(min = 2, max = 40, message = "First name(s) can not be less than 2 symbols or greater than 40")
    private String firstName;

    @OnlyAscii
    @Size(min = 2, max = 40, message = "Last name can not be less than 2 symbols or greater than 40")
    private String lastName;

    @OnlyAscii
    @Size(min = 3, max = 400, message = "Comment can not be less than 3 symbols or greater than 40")
    private String comment;

    @Email
    private String email;

    @PhoneNumber
    private String phoneNumber;
}
