package dk.ledocsystem.service.impl.property_maps.equipment;

import dk.ledocsystem.data.model.equipment.Equipment;
import dk.ledocsystem.service.api.dto.outbound.equipment.EquipmentPreviewDTO;
import org.modelmapper.PropertyMap;

public class EquipmentToPreviewDtoPropertyMap extends PropertyMap<Equipment, EquipmentPreviewDTO> {

    @Override
    protected void configure() {
        map().setResponsibleName(source.getResponsible().getName());
        map().setLocationName(source.getLocation().getName());
        map().setCategoryName(source.getCategory().getNameEn());
        map().setAuthTypeName(source.getAuthenticationType().getNameEn());
        map().setReviewTemplateName(source.getReviewTemplate().getName());
    }
}
