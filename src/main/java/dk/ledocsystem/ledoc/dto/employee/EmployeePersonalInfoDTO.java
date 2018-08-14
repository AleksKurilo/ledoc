package dk.ledocsystem.ledoc.dto.employee;

import dk.ledocsystem.ledoc.annotations.validation.MobilePhone;
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

    @MobilePhone
    private String personalPhone;

    @MobilePhone
    private String personalMobile;

    private LocalDate dateOfBirth;

    @Email
    private String privateEmail;

    private LocalDate dayOfEmployment;
}
