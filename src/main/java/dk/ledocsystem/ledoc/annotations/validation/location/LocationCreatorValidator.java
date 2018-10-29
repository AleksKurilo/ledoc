package dk.ledocsystem.ledoc.annotations.validation.location;

import dk.ledocsystem.ledoc.dto.location.LocationDTO;
import dk.ledocsystem.ledoc.model.LocationType;

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
