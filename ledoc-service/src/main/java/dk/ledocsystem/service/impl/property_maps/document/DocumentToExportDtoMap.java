package dk.ledocsystem.service.impl.property_maps.document;

import dk.ledocsystem.data.model.Location;
import dk.ledocsystem.data.model.document.Document;
import dk.ledocsystem.service.api.dto.outbound.document.DocumentExportDTO;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.Converter;
import org.modelmapper.PropertyMap;

import java.util.Set;
import java.util.stream.Collectors;

public class DocumentToExportDtoMap extends PropertyMap<Document, DocumentExportDTO> {

    @Override
    protected void configure() {
        Converter<Set<Location>, String> locationNamesConverter = context -> context.getSource() == null ? "" : StringUtils.join(context.getSource().stream().map(Location::getName).collect(Collectors.toList()), ',');
        using(locationNamesConverter).map(source.getLocations(), destination.getLocationNames());
//        using(Converters.Collection.map(Location::getName))
//                .map(source.getLocations(), destination.getLocationNames());
    }
}