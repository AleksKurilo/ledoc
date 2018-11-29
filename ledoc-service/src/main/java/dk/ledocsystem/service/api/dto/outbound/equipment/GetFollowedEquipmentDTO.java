package dk.ledocsystem.service.api.dto.outbound.equipment;

import lombok.Data;

@Data
public class GetFollowedEquipmentDTO {

    private Long id;

    private String name;

    private boolean forced;
}
