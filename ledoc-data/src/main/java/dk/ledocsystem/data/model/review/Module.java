package dk.ledocsystem.data.model.review;

import dk.ledocsystem.data.exceptions.InvalidEnumValueException;

public enum Module {
    EMPLOYEES, EQUIPMENT, SUPPLIERS, DOCUMENTS;

    public static Module fromString(String value) {
        for (Module module : values()) {
            if (module.toString().equalsIgnoreCase(value)) {
                return module;
            }
        }
        throw new InvalidEnumValueException("review.module.not.found", value);
    }
}
