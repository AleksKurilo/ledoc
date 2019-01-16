package dk.ledocsystem.service.impl.utils.diff.changes;

import lombok.Getter;

public class ValueRemovedChange extends Change {

    @Getter
    private final Object removedValue;

    public ValueRemovedChange(String propertyName, Object removedValue) {
        super(propertyName);
        this.removedValue = removedValue;
    }
}
