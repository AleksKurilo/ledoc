package dk.ledocsystem.service.impl.property_maps.location;

import dk.ledocsystem.data.model.Location;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.service.api.dto.outbound.location.LocationPreviewDTO;
import org.modelmapper.Converters;
import org.modelmapper.PropertyMap;

public class LocationToPreviewDtoPropertyMap extends PropertyMap<Location, LocationPreviewDTO> {

    @Override
    protected void configure() {
        map().setResponsibleName(source.getResponsible().getName());
        map().setCreatedBy(source.getCreatedBy().getName());
        map().setAddressLocationId(source.getAddressLocation().getId());
        map().setAddressLocationName(source.getAddressLocation().getName());
        using(Converters.Collection.map(Employee::getName))
                .map(source.getEmployees(), destination.getEmployees());
    }
}
