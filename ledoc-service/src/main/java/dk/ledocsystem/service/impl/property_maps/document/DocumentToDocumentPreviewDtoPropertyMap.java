package dk.ledocsystem.service.impl.property_maps.document;

import dk.ledocsystem.data.model.Location;
import dk.ledocsystem.data.model.Trade;
import dk.ledocsystem.data.model.document.Document;
import dk.ledocsystem.service.api.dto.outbound.document.DocumentPreviewDTO;
import org.modelmapper.Converters;
import org.modelmapper.PropertyMap;

public class DocumentToDocumentPreviewDtoPropertyMap extends PropertyMap<Document, DocumentPreviewDTO> {

    @Override
    protected void configure() {
        map().setResponsibleName(source.getResponsible().getName());
        map().setCategoryName(source.getCategory().getNameEn());
        map().setSubcategoryName(source.getSubcategory().getNameEn());
        using(Converters.Collection.map(Location::getName))
                .map(source.getLocations(), destination.getLocationNames());
        using(Converters.Collection.map(Trade::getNameEn))
                .map(source.getTrades(), destination.getTradeNames());
    }
}
