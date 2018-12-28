package dk.ledocsystem.service.impl.property_maps.document;

import dk.ledocsystem.data.model.Location;
import dk.ledocsystem.data.model.Trade;
import dk.ledocsystem.data.model.document.Document;
import dk.ledocsystem.service.api.dto.outbound.document.DocumentPreviewDTO;
import dk.ledocsystem.service.impl.property_maps.converters.DoubleNamedLocalizedConverter;
import org.modelmapper.Converters;
import org.modelmapper.PropertyMap;

public class DocumentToDocumentPreviewDtoPropertyMap extends PropertyMap<Document, DocumentPreviewDTO> {

    @Override
    protected void configure() {
        map().setResponsibleName(source.getResponsible().getName());
        using(DoubleNamedLocalizedConverter.INSTANCE).map(source.getCategory(), destination.getCategoryName());
        using(DoubleNamedLocalizedConverter.INSTANCE).map(source.getSubcategory(), destination.getSubcategoryName());
        using(Converters.Collection.map(Location::getName))
                .map(source.getLocations(), destination.getLocationNames());
        using(Converters.Collection.map((Converters.Converter<Trade, String>) DoubleNamedLocalizedConverter.INSTANCE::convert))
                .map(source.getTrades(), destination.getTradeNames());
    }
}
