package dk.ledocsystem.service.impl.property_maps.document;

import dk.ledocsystem.data.model.document.Document;
import dk.ledocsystem.service.api.dto.outbound.document.DocumentExportDTO;
import org.modelmapper.PropertyMap;

public class DocumentToExportDtoMap extends PropertyMap<Document, DocumentExportDTO> {

    @Override
    protected void configure() {
        map().setLocationNames(source.getLocation().getName());
    }
}
