package dk.ledocsystem.service.impl.property_maps.document;

import dk.ledocsystem.data.model.Location;
import dk.ledocsystem.data.model.Trade;
import dk.ledocsystem.data.model.document.Document;
import dk.ledocsystem.service.api.dto.outbound.document.DocumentEditDTO;
import org.modelmapper.Converters;
import org.modelmapper.PropertyMap;

public class DocumentToEditDtoPropertyMap extends PropertyMap<Document, DocumentEditDTO> {

    @Override
    protected void configure() {
        skip(destination.getCategory());
        skip(destination.getSubcategory());
        skip(destination.getResponsible());
        map().setResponsibleId(source.getResponsible().getId());
        map().setCategoryId(source.getCategory().getId());
        map().setSubcategoryId(source.getSubcategory().getId());
        using(Converters.Collection.map(Location::getId))
                .map(source.getLocations(), destination.getLocationIds());
        using(Converters.Collection.map(Trade::getId))
                .map(source.getTrades(), destination.getTradeIds());
    }
}
