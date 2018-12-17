package dk.ledocsystem.service.impl.property_maps.document;

import dk.ledocsystem.data.model.document.Document;
import dk.ledocsystem.service.api.dto.outbound.document.DocumentExportDTO;
import org.modelmapper.PropertyMap;

public class DocumentToExportDtoMap extends PropertyMap<Document, DocumentExportDTO> {

    @Override
    protected void configure() {
//        Converter<Set<Location>, String> locationNamesConverter = context -> context.getSource() == null ? "" : StringUtils.join(context.getSource().stream().map(Location::getName).collect(Collectors.toList()), ',');
//        using(locationNamesConverter).map(source.getLocation(), destination.getLocationNames());
        map().setLocationNames(source.getLocation().getName());
    }
}
