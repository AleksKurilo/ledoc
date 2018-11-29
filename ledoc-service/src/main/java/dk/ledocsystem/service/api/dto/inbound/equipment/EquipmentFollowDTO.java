package dk.ledocsystem.service.api.dto.inbound.equipment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentFollowDTO {

    private Long followerId;
    private boolean followed;
}
