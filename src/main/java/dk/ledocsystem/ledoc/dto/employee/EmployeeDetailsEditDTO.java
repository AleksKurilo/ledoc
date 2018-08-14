package dk.ledocsystem.ledoc.dto.employee;

import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class EmployeeDetailsEditDTO {

    private String title;

    @OnlyAscii
    @Size(max = 500)
    private String comment;

    private Boolean skillAssessed;

    private Long skillResponsibleId;

}
