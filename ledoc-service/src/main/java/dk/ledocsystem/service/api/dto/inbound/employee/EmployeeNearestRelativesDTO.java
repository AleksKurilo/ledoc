package dk.ledocsystem.service.api.dto.inbound.employee;

import dk.ledocsystem.service.api.validation.OnlyAscii;
import dk.ledocsystem.service.api.validation.PhoneNumber;
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
    @Size(max = 400)
    private String comment;

    @Email
    private String email;

    @PhoneNumber
    private String phoneNumber;
}
