package dk.ledocsystem.data.model.document;

import com.fasterxml.jackson.annotation.JsonValue;
import dk.ledocsystem.data.exceptions.InvalidEnumValueException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DocumentStatus {

    ACTIVE_WITH_REVIEW("active with review"),
    ACTIVE_WITHOUT_REVIEW("active without review");

    private final String value;

    public static DocumentStatus fromString(String stringValue) {
        for (DocumentStatus documentStatus : DocumentStatus.values()) {
            if (documentStatus.value.equalsIgnoreCase(stringValue)) {
                return documentStatus;
            }
        }
        throw new InvalidEnumValueException("document.status.not.found", stringValue);
    }

    @JsonValue
    public String value() {
        return value;
    }
}
