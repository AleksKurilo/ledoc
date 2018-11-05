package dk.ledocsystem.ledoc.service.dto;

import dk.ledocsystem.ledoc.model.equipment.ApprovalType;
import dk.ledocsystem.ledoc.model.equipment.Status;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Data
public class GetEquipmentDTO {

    private String name;

    private String idNumber;

    private Long authTypeId;

    private Long categoryId;

    private Long responsibleId;

    private Long locationId;

    private ApprovalType approvalType;

    private Period approvalRate;

    private Long reviewTemplateId;

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
