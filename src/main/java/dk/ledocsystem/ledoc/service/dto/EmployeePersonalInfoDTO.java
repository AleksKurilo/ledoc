package dk.ledocsystem.ledoc.service.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeePersonalInfoDTO {

    private String address;

    private String buildingNo;

    private String postalCode;

    private String city;

    private String personalPhone;

    private String personalMobile;

    private LocalDate dateOfBirth;

    private String privateEmail;

    private LocalDate dayOfEmployment;

    private String comment;
}
