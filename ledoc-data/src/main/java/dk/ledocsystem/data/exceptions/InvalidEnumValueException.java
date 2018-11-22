package dk.ledocsystem.data.exceptions;

import lombok.Getter;

@Getter
public class InvalidEnumValueException extends RuntimeException {

    private final String messageKey;
    private final String value;

    public InvalidEnumValueException(String messageKey, String value) {
        this.messageKey = messageKey;
        this.value = value;
    }
}
