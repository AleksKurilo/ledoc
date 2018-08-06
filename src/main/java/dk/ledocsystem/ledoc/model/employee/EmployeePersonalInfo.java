package dk.ledocsystem.ledoc.model.employee;

import dk.ledocsystem.ledoc.dto.EmployeePersonalInfoDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class EmployeePersonalInfo {

    public EmployeePersonalInfo(EmployeePersonalInfoDTO personalInfoDTO) {
        setAddress(personalInfoDTO.getAddress());
        setBuildingNo(personalInfoDTO.getBuildingNo());
        setPostalCode(personalInfoDTO.getPostalCode());
        setCity(personalInfoDTO.getCity());
        setPersonalPhone(personalInfoDTO.getPersonalPhone());
        setPersonalMobile(personalInfoDTO.getPersonalMobile());
        setDateOfBirth(personalInfoDTO.getDateOfBirth());
        setPrivateEmail(personalInfoDTO.getPrivateEmail());
        setDayOfEmployment(personalInfoDTO.getDayOfEmployment());
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
}
