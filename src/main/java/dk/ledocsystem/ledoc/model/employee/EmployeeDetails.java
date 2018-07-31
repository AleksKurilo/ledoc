package dk.ledocsystem.ledoc.model.employee;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.OneToOne;

@Getter
@Setter
@NoArgsConstructor
@ToString(of = "title")
@Embeddable
public class EmployeeDetails {

    @Column(nullable = false)
    private String title;

    @Column
    private String comment;

    @ColumnDefault("false")
    @Column(name = "skill_assesed")
    private Boolean skillAssesed;

    @OneToOne
    @Column(name = "response_of_skills_id")
    private Employee responsibleOfSkills;
}
