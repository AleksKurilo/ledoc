package dk.ledocsystem.service.impl.property_maps.location;

import dk.ledocsystem.data.model.Location;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.service.api.dto.outbound.location.LocationEditDTO;
import org.modelmapper.Converters;
import org.modelmapper.PropertyMap;

public class LocationToEditDtoPropertyMap extends PropertyMap<Location, LocationEditDTO> {

    @Override
    protected void configure() {
        skip(destination.getResponsible());
        skip(destination.getPhysicalLocations());
        map().setResponsibleId(source.getResponsible().getId());
        map().setAddressLocationId(source.getAddressLocation().getId());
        using(Converters.Collection.map(Employee::getId))
                .map(source.getEmployees(), destination.getEmployeeIds());
    }
}
