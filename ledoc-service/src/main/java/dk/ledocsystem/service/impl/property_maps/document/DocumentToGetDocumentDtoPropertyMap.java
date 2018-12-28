package dk.ledocsystem.service.impl.property_maps.document;

import dk.ledocsystem.data.model.document.Document;
import dk.ledocsystem.service.api.dto.outbound.document.GetDocumentDTO;
import dk.ledocsystem.service.impl.property_maps.converters.DoubleNamedLocalizedConverter;
import org.modelmapper.PropertyMap;


public class DocumentToGetDocumentDtoPropertyMap extends PropertyMap<Document, GetDocumentDTO> {

    @Override
    protected void configure() {
        map().setResponsible(source.getResponsible().getName());
        using(DoubleNamedLocalizedConverter.INSTANCE).map(source.getCategory(), destination.getCategory());
        using(DoubleNamedLocalizedConverter.INSTANCE).map(source.getSubcategory(), destination.getSubcategory());
    }
}
