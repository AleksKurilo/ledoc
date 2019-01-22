package dk.ledocsystem.data.model.document;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import dk.ledocsystem.data.exceptions.InvalidEnumValueException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DocumentType {
    DIGITAL("digital"),
    PHYSICAL("physical");

    private final String value;

    @JsonCreator
    public static DocumentType fromString(String stringValue) {
        for (DocumentType documentType : DocumentType.values()) {
            if (documentType.value.equalsIgnoreCase(stringValue)) {
                return documentType;
            }
        }
        throw new InvalidEnumValueException("document.type.not.found", stringValue);
    }

    @JsonValue
    public String value() {
        return value;
    }


    @Override
    public String toString() {
        return value;
    }
}
