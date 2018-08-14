package dk.ledocsystem.ledoc.model.employee;

import dk.ledocsystem.ledoc.dto.employee.EmployeePersonalInfoDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDate;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class EmployeePersonalInfo {

    EmployeePersonalInfo(EmployeePersonalInfoDTO personalInfo) {
        if (personalInfo != null) {
            setAddress(personalInfo.getAddress());
            setBuildingNo(personalInfo.getBuildingNo());
            setPostalCode(personalInfo.getPostalCode());
            setCity(personalInfo.getCity());
            setPersonalPhone(personalInfo.getPersonalPhone());
            setPersonalMobile(personalInfo.getPersonalMobile());
            setDateOfBirth(personalInfo.getDateOfBirth());
            setPrivateEmail(personalInfo.getPrivateEmail());
            setDayOfEmployment(personalInfo.getDayOfEmployment());
        }
    }

    @Column
    private String address;

    @Column(name = "building_no", length = 40)
    private String buildingNo;

    @Column(name = "postal_code", length = 40)
    private String postalCode;

    @Column(length = 40)
    private String city;

    @Column(name = "personal_phone", length = 40)
    private String personalPhone;

    @Column(name = "personal_mobile", length = 40)
    private String personalMobile;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "private_email", length = 40)
    private String privateEmail;

    @Column(name = "day_of_employment")
    private LocalDate dayOfEmployment;

    void updateProperties(EmployeePersonalInfoDTO personalInfo) {
        if (personalInfo != null) {
            setAddress(defaultIfNull(personalInfo.getAddress(), getAddress()));
            setBuildingNo(defaultIfNull(personalInfo.getBuildingNo(), getBuildingNo()));
            setPostalCode(defaultIfNull(personalInfo.getPostalCode(), getPostalCode()));
            setCity(defaultIfNull(personalInfo.getCity(), getCity()));
            setPersonalPhone(defaultIfNull(personalInfo.getPersonalPhone(), getPersonalPhone()));
            setPersonalMobile(defaultIfNull(personalInfo.getPersonalMobile(), getPersonalMobile()));
            setDateOfBirth(defaultIfNull(personalInfo.getDateOfBirth(), getDateOfBirth()));
            setPrivateEmail(defaultIfNull(personalInfo.getPrivateEmail(), getPrivateEmail()));
            setDayOfEmployment(defaultIfNull(personalInfo.getDayOfEmployment(), getDayOfEmployment()));
        }
    }
}
