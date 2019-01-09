package dk.ledocsystem.service.impl.property_maps.customers;

import dk.ledocsystem.data.model.Customer;
import dk.ledocsystem.service.api.dto.outbound.customer.FullCustomerExportDTO;
import org.modelmapper.PropertyMap;

public class CustomerToFullExportDtoMap extends PropertyMap<Customer, FullCustomerExportDTO> {

    @Override
    protected void configure() {
        map().setPointOfContact(source.getPointOfContact().getName());
    }
}