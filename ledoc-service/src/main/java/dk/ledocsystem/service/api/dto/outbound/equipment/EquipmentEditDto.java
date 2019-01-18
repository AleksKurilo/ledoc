package dk.ledocsystem.service.api.dto.outbound.equipment;

import dk.ledocsystem.data.model.equipment.ApprovalType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class EquipmentEditDto extends GetEquipmentDTO {

    private Long authTypeId;

    private Long categoryId;

    private Long locationId;

    private ApprovalType approvalType;
}
