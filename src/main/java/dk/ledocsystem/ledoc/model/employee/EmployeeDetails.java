package dk.ledocsystem.ledoc.model.employee;

import dk.ledocsystem.ledoc.dto.EmployeeDetailsDTO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Setter
@NoArgsConstructor
@ToString(of = "title")
@Embeddable
public class EmployeeDetails {

    public EmployeeDetails(EmployeeDetailsDTO employeeDetailsDTO) {
        setTitle(employeeDetailsDTO.getTitle());
        setComment(employeeDetailsDTO.getComment());
        setSkillAssessed(employeeDetailsDTO.isSkillAssessed());
    }

    @Column(nullable = false)
    private String title;

    @Column
    private String comment;

    @ColumnDefault("false")
    @Column(name = "skill_assessed")
    private Boolean skillAssessed;

    @ManyToOne
    @JoinColumn(name = "responsible_of_skills_id")
    private Employee responsibleOfSkills;
}
