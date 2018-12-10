package dk.ledocsystem.service.api.dto.outbound.document;

import dk.ledocsystem.data.model.document.DocumentSource;
import dk.ledocsystem.data.model.document.DocumentStatus;
import dk.ledocsystem.data.model.document.DocumentType;
import dk.ledocsystem.data.model.equipment.ApprovalType;
import dk.ledocsystem.service.api.dto.inbound.document.DocumentCategoryDTO;
import dk.ledocsystem.service.api.dto.inbound.document.DocumentSubcategoryDTO;
import dk.ledocsystem.service.api.dto.inbound.employee.EmployeeDTO;
import dk.ledocsystem.service.api.dto.inbound.equipment.EquipmentDTO;
import dk.ledocsystem.service.api.dto.inbound.location.LocationDTO;
import lombok.Data;

import java.time.LocalDate;
import java.time.Period;

@Data
public class GetDocumentDTO {

    private Long id;

    private String name;

    private String archiveReason;

    private String comment;

    private DocumentType type;

    private DocumentSource source;

    private DocumentStatus status;

    private ApprovalType approvalType;

    private Period approvalRate;

    private Long reviewTemplateId;

    private boolean personal;

    private EmployeeDTO employee;

    private EquipmentDTO equipment;

    private EmployeeDTO responsible;

    private DocumentCategoryDTO category;

    private DocumentSubcategoryDTO subcategory;

    private LocationDTO location;

    private LocalDate createOn;
}
