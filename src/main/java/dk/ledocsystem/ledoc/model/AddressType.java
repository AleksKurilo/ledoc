package dk.ledocsystem.ledoc.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import dk.ledocsystem.ledoc.exceptions.InvalidEnumValueException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum AddressType {
    DEPARTMENT("Department"), HEAD_OFFICE("Head office");

    private final String value;

    @JsonCreator
    public static AddressType fromString(String value) {
        for (AddressType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new InvalidEnumValueException("address.type.not.found", value);
    }

    @JsonValue
    public String value() {
        return value;
    }
}
