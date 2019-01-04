package dk.ledocsystem.service.api.dto.outbound.document;

import dk.ledocsystem.data.model.document.DocumentSource;
import dk.ledocsystem.data.model.document.DocumentStatus;
import dk.ledocsystem.data.model.document.DocumentType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class GetDocumentDTO {

    private Long id;

    private String name;

    private String idNumber;

    private DocumentType type;

    private DocumentSource source;

    private DocumentStatus status;

    private boolean personal;

    private boolean read;

    private String responsible;

    private String category;

    private String subcategory;

    private LocalDate nextReviewDate;

    private LocalDate createOn;
}
