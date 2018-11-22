package dk.ledocsystem.service.impl.property_maps.location;

import dk.ledocsystem.data.model.Location;
import dk.ledocsystem.service.api.dto.outbound.location.PhysicalLocationDTO;
import org.modelmapper.PropertyMap;

public class LocationToPhysicalLocationDtoPropertyMap extends PropertyMap<Location, PhysicalLocationDTO> {

    @Override
    protected void configure() {
        map().setAddressLocationId(source.getAddressLocation().getId());
    }
}
