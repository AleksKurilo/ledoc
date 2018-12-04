package dk.ledocsystem.service.api.dto.inbound.employee;

import dk.ledocsystem.service.api.validation.NonCyrillic;
import dk.ledocsystem.service.api.validation.PhoneNumber;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class EmployeePersonalInfoDTO {

    @NonCyrillic
    private String address;

    @Size(min = 1, max = 40)
    @NonCyrillic
    private String buildingNo;

    @NonCyrillic
    private String postalCode;

    @NonCyrillic
    private String city;

    @PhoneNumber
    private String personalPhone;

    @PhoneNumber
    private String personalMobile;

    private LocalDate dateOfBirth;

    @Email
    private String privateEmail;

    private LocalDate dayOfEmployment;

    @NonCyrillic
    @Size(max = 400)
    private String comment;
}
