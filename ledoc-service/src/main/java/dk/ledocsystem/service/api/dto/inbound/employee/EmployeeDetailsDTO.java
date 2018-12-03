package dk.ledocsystem.service.api.dto.inbound.employee;

import dk.ledocsystem.service.api.validation.NonCyrillic;
import dk.ledocsystem.service.api.validation.review.ReviewDetails;
import lombok.Data;

import javax.validation.constraints.Size;
import java.time.Period;

@Data
@ReviewDetails
public class EmployeeDetailsDTO {

    @NonCyrillic
    @Size(max = 400)
    private String comment;

    private boolean skillAssessed;

    private Period reviewFrequency;

    private Long skillResponsibleId;

    private Long reviewTemplateId;

}
