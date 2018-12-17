package dk.ledocsystem.data.model.document;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import dk.ledocsystem.data.exceptions.InvalidEnumValueException;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DocumentSource {
    ONLINE("online"),
    WEB("web");

    private final String value;

    @JsonCreator
    public static DocumentSource fromString(String stringValue) {
        for (DocumentSource documentSource : DocumentSource.values()) {
            if (documentSource.value.equalsIgnoreCase(stringValue)) {
                return documentSource;
            }
        }
        throw new InvalidEnumValueException("document.source.not.found", stringValue);
    }

    @JsonValue
    public String value() {
        return value;
    }
}
