package dk.ledocsystem.ledoc.model.employee;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

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
    @Column(name = "skill_assessed")
    private Boolean skillAssessed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_of_skills_id")
    private Employee responsibleOfSkills;
}
