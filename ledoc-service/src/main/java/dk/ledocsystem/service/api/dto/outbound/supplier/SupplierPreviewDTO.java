package dk.ledocsystem.service.api.dto.outbound.supplier;

import lombok.Data;

import java.time.Period;
import java.util.Set;

@Data
public class SupplierPreviewDTO {

    private String name;

    private String description;

    private String contactPhone;

    private String contactEmail;

    private Period approvalRate;

    private boolean archived;

    private String archiveReason;

    private String reviewTemplateName;

    private String responsibleName;

    private String reviewResponsibleName;

    private String categoryName;

    private Set<String> locationNames;
}
