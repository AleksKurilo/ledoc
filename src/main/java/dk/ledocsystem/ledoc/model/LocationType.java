package dk.ledocsystem.ledoc.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import dk.ledocsystem.ledoc.exceptions.InvalidLocationTypeException;
import lombok.NonNull;

public enum LocationType {

    ADDRESS, PHYSICAL;

    @JsonCreator
    public static LocationType fromString(@NonNull String locationType) {
        for (LocationType type : LocationType.values()) {
            if (type.toString().equalsIgnoreCase(locationType)) {
                return type;
            }
        }
        throw new InvalidLocationTypeException(locationType);
    }
}
