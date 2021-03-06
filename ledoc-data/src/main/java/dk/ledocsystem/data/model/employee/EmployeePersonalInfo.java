package dk.ledocsystem.data.model.employee;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class EmployeePersonalInfo {

    @Column
    private String address;

    @Column(name = "building_no", length = 40)
    private String buildingNo;

    @Column(name = "postal_code", length = 40)
    private String postalCode;

    @Column(length = 40)
    private String city;

    @Column(name = "personal_phone", length = 25)
    private String personalPhone;

    @Column(name = "personal_mobile", length = 25)
    private String personalMobile;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "private_email", length = 40)
    private String privateEmail;

    @Column(name = "day_of_employment")
    private LocalDate dayOfEmployment;

    @Column(name = "personal_comment", length = 400)
    private String comment;
}
