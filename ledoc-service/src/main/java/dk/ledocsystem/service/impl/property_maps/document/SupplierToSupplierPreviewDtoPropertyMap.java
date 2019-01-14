package dk.ledocsystem.service.impl.property_maps.document;


import dk.ledocsystem.data.model.Location;
import dk.ledocsystem.data.model.supplier.Supplier;
import dk.ledocsystem.service.api.dto.outbound.supplier.SupplierPreviewDTO;
import dk.ledocsystem.service.impl.property_maps.converters.DoubleNamedLocalizedConverter;
import org.modelmapper.Converters;
import org.modelmapper.PropertyMap;

public class SupplierToSupplierPreviewDtoPropertyMap extends PropertyMap<Supplier, SupplierPreviewDTO> {

    @Override
    protected void configure() {
        map().setResponsibleName(source.getResponsible().getName());
        map().setReviewResponsibleName(source.getReviewResponsible().getName());
        using(DoubleNamedLocalizedConverter.INSTANCE).map(source.getCategory(), destination.getCategoryName());
        using(Converters.Collection.map(Location::getName))
                .map(source.getLocations(), destination.getLocationNames());
    }
}
