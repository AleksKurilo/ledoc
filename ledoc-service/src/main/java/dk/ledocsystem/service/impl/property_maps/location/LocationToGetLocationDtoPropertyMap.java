package dk.ledocsystem.service.impl.property_maps.location;

import dk.ledocsystem.data.model.Location;
import dk.ledocsystem.service.api.dto.outbound.location.GetLocationDTO;
import org.modelmapper.PropertyMap;

public class LocationToGetLocationDtoPropertyMap extends PropertyMap<Location, GetLocationDTO> {

    @Override
    protected void configure() {
        map().setResponsible(source.getResponsible().getName());
        map().setAddressLocationId(source.getAddressLocation().getId());
    }
}
