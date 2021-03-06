package dk.ledocsystem.service.impl.property_maps.equipment;

import dk.ledocsystem.data.model.equipment.Equipment;
import dk.ledocsystem.service.api.dto.outbound.equipment.GetEquipmentDTO;
import org.modelmapper.PropertyMap;

public class EquipmentToGetEquipmentDtoPropertyMap extends PropertyMap<Equipment, GetEquipmentDTO> {

    @Override
    protected void configure() {
        map().setLocation(source.getLocation().getName());
        map().setBorrowerAvatar(source.getLoan().getBorrower().getAvatar());
    }
}
