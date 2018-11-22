package dk.ledocsystem.service.api.dto.inbound.employee;

import dk.ledocsystem.service.api.validation.OnlyAscii;
import dk.ledocsystem.service.api.validation.PhoneNumber;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Data
public class EmployeePersonalInfoDTO {

    private String address;

    @Size(min = 1, max = 40)
    private String buildingNo;

    private String postalCode;

    private String city;

    @PhoneNumber
    private String personalPhone;

    @PhoneNumber
    private String personalMobile;

    private LocalDate dateOfBirth;

    @Email
    private String privateEmail;

    private LocalDate dayOfEmployment;

    @OnlyAscii
    @Size(max = 400)
    private String comment;
}
