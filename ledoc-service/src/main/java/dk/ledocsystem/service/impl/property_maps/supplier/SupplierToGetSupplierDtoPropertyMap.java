package dk.ledocsystem.service.impl.property_maps.supplier;

import dk.ledocsystem.data.model.supplier.Supplier;
import dk.ledocsystem.service.api.dto.outbound.supplier.GetSupplierDTO;
import dk.ledocsystem.service.impl.property_maps.converters.DoubleNamedLocalizedConverter;
import org.modelmapper.PropertyMap;

public class SupplierToGetSupplierDtoPropertyMap extends PropertyMap<Supplier, GetSupplierDTO> {

    @Override
    protected void configure() {
        using(DoubleNamedLocalizedConverter.INSTANCE).map(source.getCategory(), destination.getCategory());
        map().setResponsible(source.getResponsible().getName());
        map().setReviewResponsible(source.getReviewResponsible().getName());
    }
}
