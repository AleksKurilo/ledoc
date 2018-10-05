package dk.ledocsystem.ledoc.service.exceptions;

import dk.ledocsystem.ledoc.exceptions.LedocException;

public class PropertyValidationException extends LedocException {

    private final String propertyName;

    public PropertyValidationException(String propertyName, String messageKey, Object... params) {
        super(messageKey, params);
        this.propertyName = propertyName;
    }
}
