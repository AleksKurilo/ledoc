package dk.ledocsystem.service.impl.utils.diff.changes;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class CollectionChange extends Change {

    private List<ValueAddedChange> valueAddedChanges = new ArrayList<>();
    private List<ValueRemovedChange> valueRemovedChanges = new ArrayList<>();

    public CollectionChange(String propertyName) {
        super(propertyName);
    }

    public void addValueAdded(ValueAddedChange valueAddedChange) {
        valueAddedChanges.add(valueAddedChange);
    }

    public void addValueRemoved(ValueRemovedChange valueRemovedChange) {
        valueRemovedChanges.add(valueRemovedChange);
    }

    public boolean hasAnyChange() {
        return !valueAddedChanges.isEmpty() || !valueRemovedChanges.isEmpty();
    }

    public List<?> getAddedValues() {
        return Lists.transform(valueAddedChanges, ValueAddedChange::getAddedValue);
    }

    public List<?> getRemovedValues() {
        return Lists.transform(valueRemovedChanges, ValueRemovedChange::getRemovedValue);
    }
}
