package dk.ledocsystem.service.impl.property_maps.document;

import dk.ledocsystem.data.model.document.Document;
import dk.ledocsystem.service.api.dto.outbound.document.GetDocumentDTO;
import org.modelmapper.PropertyMap;


public class DocumentToGetDocumentDtoPropertyMap extends PropertyMap<Document, GetDocumentDTO> {

    @Override
    protected void configure() {
        map().setResponsible(source.getResponsible().getName());
        map().setCategory(source.getCategory().getNameEn());
        map().setSubcategory(source.getSubcategory().getNameEn());
    }
}
