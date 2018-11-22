package dk.ledocsystem.service.api.validation.location;

import dk.ledocsystem.service.api.dto.inbound.location.LocationDTO;
import dk.ledocsystem.data.model.LocationType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class LocationCreatorValidator implements ConstraintValidator<LocationCreator, LocationDTO> {

    @Override
    public boolean isValid(LocationDTO value, ConstraintValidatorContext context) {
        LocationType locationType = value.getType();
        if (locationType == LocationType.ADDRESS) {
            return value.getAddress() != null;
        } else {
            return value.getAddressLocationId() != null;
        }
    }
}
