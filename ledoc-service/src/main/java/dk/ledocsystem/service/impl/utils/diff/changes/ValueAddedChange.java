package dk.ledocsystem.service.impl.utils.diff.changes;

import lombok.Getter;

public class ValueAddedChange extends Change {

    @Getter
    private final Object addedValue;

    public ValueAddedChange(String propertyName, Object addedValue) {
        super(propertyName);
        this.addedValue = addedValue;
    }
}
