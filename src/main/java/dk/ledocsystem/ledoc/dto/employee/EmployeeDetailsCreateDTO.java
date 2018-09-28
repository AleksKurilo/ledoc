package dk.ledocsystem.ledoc.dto.employee;

import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import dk.ledocsystem.ledoc.annotations.validation.review.ReviewDetails;
import lombok.Data;

import javax.validation.constraints.Size;
import java.time.Period;

@Data
@ReviewDetails
public class EmployeeDetailsCreateDTO {

    @OnlyAscii
    @Size(max = 400)
    private String comment;

    private boolean skillAssessed;

    private Period reviewFrequency;

    private Long skillResponsibleId;

}
