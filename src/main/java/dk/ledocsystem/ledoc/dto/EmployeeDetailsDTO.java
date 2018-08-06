package dk.ledocsystem.ledoc.dto;

import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class EmployeeDetailsDTO {

    @NotNull(message = "Title must be not null")

    private String title;

    @OnlyAscii
    @Size(max = 500)
    private String comment;

    private boolean skillAssessed;

    private Long skillResponsibleId;

}
