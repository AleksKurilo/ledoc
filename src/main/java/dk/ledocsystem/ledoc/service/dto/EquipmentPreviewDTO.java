package dk.ledocsystem.ledoc.service.dto;

import dk.ledocsystem.ledoc.model.equipment.ApprovalType;
import dk.ledocsystem.ledoc.model.equipment.Status;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Data
public class EquipmentPreviewDTO {

    private String name;

    private String idNumber;

    private String authTypeName;

    private String categoryName;

    private String responsibleName;

    private String locationName;

    private ApprovalType approvalType;

    private Period approvalRate;

    private String reviewTemplateName;

    private Status status;

    private String manufacturer;

    private LocalDate purchaseDate;

    private String serialNumber;

    private String localId;

    private LocalDate warrantyDate;

    private BigDecimal price;

    private String comment;

    private String avatar;

    private Boolean readyToLoan;
}
