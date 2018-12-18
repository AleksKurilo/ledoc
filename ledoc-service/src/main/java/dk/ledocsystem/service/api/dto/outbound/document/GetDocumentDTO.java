package dk.ledocsystem.service.api.dto.outbound.document;

import dk.ledocsystem.data.model.document.DocumentSource;
import dk.ledocsystem.data.model.document.DocumentStatus;
import dk.ledocsystem.data.model.document.DocumentType;
import lombok.Data;

import java.time.LocalDate;
import java.time.Period;
import java.util.Set;

@Data
public class GetDocumentDTO {

    private Long id;

    private String name;

    private String file;

    private String archiveReason;

    private String comment;

    private DocumentType type;

    private DocumentSource source;

    private DocumentStatus status;

    private Period approvalRate;

    private Long reviewTemplateId;

    private boolean personal;

    private String responsible;

    private String category;

    private String subcategory;

    private Set<String> locationNames;

    private LocalDate createOn;
}
