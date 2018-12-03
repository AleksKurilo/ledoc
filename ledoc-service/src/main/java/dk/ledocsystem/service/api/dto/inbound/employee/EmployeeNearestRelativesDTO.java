package dk.ledocsystem.service.api.dto.inbound.employee;

import dk.ledocsystem.service.api.validation.NonCyrillic;
import dk.ledocsystem.service.api.validation.PhoneNumber;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
public class EmployeeNearestRelativesDTO {

    @NonCyrillic
    @Size(min = 2, max = 40)
    private String firstName;

    @NonCyrillic
    @Size(min = 2, max = 40)
    private String lastName;

    @NonCyrillic
    @Size(max = 400)
    private String comment;

    @Email
    private String email;

    @PhoneNumber
    private String phoneNumber;
}
