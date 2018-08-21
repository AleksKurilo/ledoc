package dk.ledocsystem.ledoc.annotations.validation.location;

import dk.ledocsystem.ledoc.dto.location.LocationCreateDTO;
import dk.ledocsystem.ledoc.model.LocationType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class LocationCreatorValidatorForCreateDTO implements ConstraintValidator<LocationCreator, LocationCreateDTO> {

    @Override
    public boolean isValid(LocationCreateDTO value, ConstraintValidatorContext context) {
        LocationType locationType = value.getType();
        if (locationType == LocationType.ADDRESS) {
            return value.getAddress() != null;
        } else {
            return value.getAddressLocationId() != null;
        }
    }
}
