package dk.ledocsystem.service.impl.property_maps.document;

import dk.ledocsystem.data.model.Location;
import dk.ledocsystem.data.model.document.Document;
import dk.ledocsystem.service.api.dto.outbound.document.GetDocumentDTO;
import org.modelmapper.Converters;
import org.modelmapper.PropertyMap;


public class DocumentToGetDocumentDtoPropertyMap extends PropertyMap<Document, GetDocumentDTO> {

    @Override
    protected void configure() {
        map().setEmployee(source.getEmployee().getName());
        map().setEquipment(source.getEquipment().getName());
        map().setResponsible(source.getResponsible().getName());
        map().setCategory(source.getCategory().getNameEn());
        map().setSubcategory(source.getSubcategory().getNameEn());
        using(Converters.Collection.map(Location::getName))
                .map(source.getLocations(), destination.getLocationNames());
    }
}
