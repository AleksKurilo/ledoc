package dk.ledocsystem.service.impl.property_maps.equipment;

import dk.ledocsystem.data.model.equipment.FollowedEquipment;
import dk.ledocsystem.service.api.dto.outbound.equipment.GetFollowedEquipmentDTO;
import org.modelmapper.PropertyMap;

public class FollowedEquipmentToGetFollowedEquipmentDtoMap extends PropertyMap<FollowedEquipment, GetFollowedEquipmentDTO> {

    @Override
    protected void configure() {
        map().setId(source.getFollowed().getId());
        map().setName(source.getFollowed().getName());
    }
}
