package dk.ledocsystem.service.api.dto.outbound.document;

import dk.ledocsystem.data.model.document.DocumentSource;
import dk.ledocsystem.data.model.document.DocumentStatus;
import dk.ledocsystem.data.model.document.DocumentType;
import dk.ledocsystem.data.model.equipment.ApprovalType;
import lombok.Data;

import java.time.LocalDate;
import java.time.Period;
import java.util.Set;

@Data
public class DocumentPreviewDTO {

    private String name;

    private String file;

    private boolean archived;

    private String archiveReason;

    private String comment;

    private DocumentType type;

    private DocumentSource source;

    private DocumentStatus status;

    private ApprovalType approvalType;

    private Period approvalRate;

    private String reviewTemplateName;

    private boolean personal;

    private String employeeName;

    private String equipmentName;

    private String responsibleName;

    private String categoryName;

    private String subcategoryName;

    private Set<String> locationNames;

    private LocalDate createOn;

    private LocalDate lastUpdate;
}
