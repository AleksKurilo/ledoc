package dk.ledocsystem.service.api.dto.inbound.employee;

import dk.ledocsystem.service.api.validation.NonCyrillic;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;
import java.time.Period;

@Data
public class EmployeeDetailsDTO {

    @NonCyrillic
    @Size(max = 400)
    private String comment;

    private boolean skillAssessed;

    @NotNull(groups = MustBeSkillAssessed.class)
    private Period reviewFrequency;

    @NotNull(groups = MustBeSkillAssessed.class)
    private Long skillResponsibleId;

    @NotNull(groups = MustBeSkillAssessed.class)
    private Long reviewTemplateId;

    interface MustBeSkillAssessed {
        // validation group marker interface
    }

    public Class<?>[] getValidationGroups() {
        return (skillAssessed)
                ? new Class[] {MustBeSkillAssessed.class, Default.class}
                : new Class[] {Default.class};
    }

}
