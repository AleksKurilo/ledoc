package dk.ledocsystem.ledoc.dto;

import dk.ledocsystem.ledoc.annotations.validation.document.ValidDocument;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Setter
@Getter
@ValidDocument
public class DocumentDTO {

    private Long id;

    private Long employeeId;

    private Long equipmentId;

    @NotNull
    private String file;

    private LocalDate createOn;

    private LocalDate lastUpdate;
}
