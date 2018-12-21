package dk.ledocsystem.service.impl.property_maps.equipment;

import dk.ledocsystem.data.model.equipment.Equipment;
import dk.ledocsystem.service.api.dto.outbound.equipment.EquipmentPreviewDTO;
import dk.ledocsystem.service.impl.property_maps.converters.DoubleNamedLocalizedConverter;
import org.modelmapper.PropertyMap;

public class EquipmentToPreviewDtoPropertyMap extends PropertyMap<Equipment, EquipmentPreviewDTO> {

    @Override
    protected void configure() {
        map().setResponsibleName(source.getResponsible().getName());
        map().setLocationName(source.getLocation().getName());
        using(DoubleNamedLocalizedConverter.INSTANCE).map(source.getCategory(), destination.getCategoryName());
        using(DoubleNamedLocalizedConverter.INSTANCE).map(source.getAuthenticationType(), destination.getAuthTypeName());
        map().setReviewTemplateName(source.getReviewTemplate().getName());
    }
}
