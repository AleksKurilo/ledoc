package dk.ledocsystem.service.api.dto.outbound.equipment;

import dk.ledocsystem.data.model.equipment.ApprovalType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Data
@EqualsAndHashCode(callSuper = true)
public class EquipmentEditDto extends GetEquipmentDTO {

    private String idNumber;

    private Long authTypeId;

    private Long categoryId;

    private Long responsibleId;

    private Long locationId;

    private ApprovalType approvalType;

    private Period approvalRate;

    private Long reviewTemplateId;

    private String manufacturer;

    private LocalDate purchaseDate;

    private String serialNumber;

    private LocalDate warrantyDate;

    private BigDecimal price;

    private String comment;
}
