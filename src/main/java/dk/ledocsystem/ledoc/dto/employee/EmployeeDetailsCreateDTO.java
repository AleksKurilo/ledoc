package dk.ledocsystem.ledoc.dto.employee;

import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import dk.ledocsystem.ledoc.annotations.validation.review.ReviewDetails;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Period;

@Data
@ReviewDetails
public class EmployeeDetailsCreateDTO {

    @NotNull
    private String title;

    @OnlyAscii
    @Size(max = 500)
    private String comment;

    @NotNull
    private Boolean skillAssessed;

    private Period reviewFrequency;

    private Long skillResponsibleId;

}
