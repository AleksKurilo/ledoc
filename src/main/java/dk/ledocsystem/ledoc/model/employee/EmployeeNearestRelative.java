package dk.ledocsystem.ledoc.model.employee;

import dk.ledocsystem.ledoc.dto.employee.EmployeeNearestRelativesDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class EmployeeNearestRelative {

    EmployeeNearestRelative(EmployeeNearestRelativesDTO nearestRelatives) {
        if (nearestRelatives != null) {
            setFirstName(nearestRelatives.getFirstName());
            setLastName(nearestRelatives.getLastName());
            setComment(nearestRelatives.getComment());
            setEmail(nearestRelatives.getEmail());
            setPhoneNumber(nearestRelatives.getPhoneNumber());
        }
    }

    @Column(name = "rel_first_name", length = 40)
    private String firstName;

    @Column(name = "rel_last_name", length = 40)
    private String lastName;

    @Column(name = "rel_comment", length = 400)
    private String comment;

    @Column(name = "rel_email", length = 40)
    private String email;

    @Column(name = "rel_phone_number", length = 40)
    private String phoneNumber;

    void updateProperties(EmployeeNearestRelativesDTO nearestRelatives) {
        if (nearestRelatives != null) {
            setFirstName(defaultIfNull(nearestRelatives.getFirstName(), getFirstName()));
            setLastName(defaultIfNull(nearestRelatives.getLastName(), getLastName()));
            setComment(defaultIfNull(nearestRelatives.getComment(), getComment()));
            setEmail(defaultIfNull(nearestRelatives.getEmail(), getEmail()));
            setPhoneNumber(defaultIfNull(nearestRelatives.getPhoneNumber(), getPhoneNumber()));
        }
    }
}
