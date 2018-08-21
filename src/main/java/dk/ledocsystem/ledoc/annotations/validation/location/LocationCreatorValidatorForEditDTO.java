package dk.ledocsystem.ledoc.annotations.validation.location;

import dk.ledocsystem.ledoc.dto.location.LocationEditDTO;
import dk.ledocsystem.ledoc.model.LocationType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class LocationCreatorValidatorForEditDTO implements ConstraintValidator<LocationCreator, LocationEditDTO> {

    @Override
    public boolean isValid(LocationEditDTO value, ConstraintValidatorContext context) {
        LocationType locationType = value.getType();

        if (locationType == null) {
            return true;
        } else if (locationType == LocationType.ADDRESS) {
            return value.getAddress() != null;
        } else {
            return value.getAddressLocationId() != null;
        }
    }
}
