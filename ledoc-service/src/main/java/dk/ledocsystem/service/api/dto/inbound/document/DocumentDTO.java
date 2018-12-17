package dk.ledocsystem.service.api.dto.inbound.document;

import dk.ledocsystem.data.model.document.DocumentSource;
import dk.ledocsystem.data.model.document.DocumentStatus;
import dk.ledocsystem.data.model.document.DocumentType;
import dk.ledocsystem.data.model.equipment.ApprovalType;
import dk.ledocsystem.service.api.validation.document.ValidDocument;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.Period;

@Setter
@Getter
@ValidDocument
public class DocumentDTO {

    private Long id;

    @NotNull
    @Size(min = 2, max = 40)
    private String name;

    @NotNull
    private String file;

    private boolean archived;

    private String archiveReason;

    @Size(max = 255)
    private String comment;

    @NotNull
    private DocumentType type;

    @NotNull
    private DocumentSource source;

    @NotNull
    private DocumentStatus status;

    @NotNull
    private ApprovalType approvalType;

    private Period approvalRate;

    private Long reviewTemplateId;

    private boolean personal;

    private Long employeeId;

    private Long equipmentId;

    @NotNull
    private Long responsibleId;

    @NotNull
    private Long categoryId;

    @NotNull
    private Long subcategoryId;

    @NotNull
    private Long locationId;

    @NotNull
    private Long tradeId;

    private LocalDate createOn;

    private LocalDate lastUpdate;


}
