package dk.ledocsystem.service.impl.utils.diff.changes;

import lombok.Getter;

public class ValueChange extends Change {

    @Getter
    private final Object left, right;

    public ValueChange(String propertyName, Object leftValue, Object rightValue) {
        super(propertyName);
        this.left = leftValue;
        this.right = rightValue;
    }
}
