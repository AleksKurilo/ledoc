package dk.ledocsystem.service.impl.property_maps.equipment;

import dk.ledocsystem.data.model.equipment.Equipment;
import dk.ledocsystem.service.api.dto.outbound.equipment.GetEquipmentDTO;
import dk.ledocsystem.service.impl.property_maps.converters.DoubleNamedLocalizedConverter;
import org.modelmapper.PropertyMap;

public class EquipmentToGetEquipmentDtoPropertyMap extends PropertyMap<Equipment, GetEquipmentDTO> {

    @Override
    protected void configure() {
        map().setLocation(source.getLocation().getName());
        map().setResponsible(source.getResponsible().getName());
        map().setResponsibleId(source.getResponsible().getId());
        map().setReviewTemplateId(source.getReviewTemplate().getId());
        map().setSimpleReview(source.getReviewTemplate().isSimple());
        using(DoubleNamedLocalizedConverter.INSTANCE).map(source.getCategory(), destination.getCategory());
        using(DoubleNamedLocalizedConverter.INSTANCE).map(source.getAuthenticationType(), destination.getAuthenticationType());
        map().getLoan().setBorrower(source.getLoan().getBorrower().getName());
        map().getLoan().setBorrowerAvatar(source.getLoan().getBorrower().getAvatar());
    }
}
