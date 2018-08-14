package dk.ledocsystem.ledoc.model.employee;

import dk.ledocsystem.ledoc.dto.employee.EmployeeDetailsCreateDTO;
import dk.ledocsystem.ledoc.dto.employee.EmployeeDetailsEditDTO;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

@Getter
@Setter
@NoArgsConstructor
@ToString(of = "title")
@Embeddable
public class EmployeeDetails {

    EmployeeDetails(@NonNull EmployeeDetailsCreateDTO details) {
        setTitle(details.getTitle());
        setComment(details.getComment());
        setSkillAssessed(details.isSkillAssessed());
    }

    @Column(nullable = false)
    private String title;

    @Column
    private String comment;

    @ColumnDefault("false")
    @Column(name = "skill_assessed")
    private Boolean skillAssessed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_of_skills_id")
    private Employee responsibleOfSkills;

    void updateProperties(EmployeeDetailsEditDTO details) {
        if (details != null) {
            setTitle(defaultIfNull(details.getTitle(), getTitle()));
            setComment(defaultIfNull(details.getComment(), getComment()));
            setSkillAssessed(defaultIfNull(details.getSkillAssessed(), getSkillAssessed()));
        }
    }
}
