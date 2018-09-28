package dk.ledocsystem.ledoc.model.employee;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class EmployeeNearestRelative {

    @Column(name = "rel_first_name", length = 40)
    private String firstName;

    @Column(name = "rel_last_name", length = 40)
    private String lastName;

    @Column(name = "rel_comment", length = 400)
    private String comment;

    @Column(name = "rel_email", length = 40)
    private String email;

    @Column(name = "rel_phone_number", length = 25)
    private String phoneNumber;

}
