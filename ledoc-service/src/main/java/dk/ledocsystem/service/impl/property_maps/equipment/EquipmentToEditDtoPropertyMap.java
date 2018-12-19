package dk.ledocsystem.service.impl.property_maps.equipment;

import dk.ledocsystem.data.model.equipment.Equipment;
import dk.ledocsystem.service.api.dto.outbound.equipment.EquipmentEditDto;
import org.modelmapper.PropertyMap;

public class EquipmentToEditDtoPropertyMap extends PropertyMap<Equipment, EquipmentEditDto> {

    @Override
    protected void configure() {
        skip(destination.getLocation());
        skip(destination.getCategory());
        skip(destination.getResponsible());
        skip(destination.getAuthenticationType());
        map().setResponsibleId(source.getResponsible().getId());
        map().setLocationId(source.getLocation().getId());
        map().setCategoryId(source.getCategory().getId());
        map().setAuthTypeId(source.getAuthenticationType().getId());
        map().setReviewTemplateId(source.getReviewTemplate().getId());
    }
}
