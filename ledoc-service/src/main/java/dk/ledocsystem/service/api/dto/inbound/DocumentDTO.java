package dk.ledocsystem.service.api.dto.inbound;

import dk.ledocsystem.service.api.validation.document.ValidDocument;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Setter
@Getter
@ValidDocument
public class DocumentDTO {

    private Long id;

    @NotNull
    @Size(min = 2, max = 40)
    private String name;

    private Long employeeId;

    private Long equipmentId;

    @NotNull
    private String file;

    private LocalDate createOn;

    private LocalDate lastUpdate;
}
