package dk.ledocsystem.data.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import dk.ledocsystem.data.exceptions.InvalidEnumValueException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum LocationType {

    ADDRESS("Address"), PHYSICAL("Physical");

    private final String value;

    @JsonCreator
    public static LocationType fromString(@NonNull String locationType) {
        for (LocationType type : LocationType.values()) {
            if (type.value.equalsIgnoreCase(locationType)) {
                return type;
            }
        }
        throw new InvalidEnumValueException("location.type.not.found", locationType);
    }

    @JsonValue
    public String value() {
        return value;
    }
}
