package dk.ledocsystem.service.impl.utils.diff.comparators;

import com.google.common.collect.Sets;
import dk.ledocsystem.service.impl.utils.diff.changes.CollectionChange;
import dk.ledocsystem.service.impl.utils.diff.changes.ValueAddedChange;
import dk.ledocsystem.service.impl.utils.diff.changes.ValueRemovedChange;

import javax.validation.constraints.NotNull;
import java.util.Set;

public class SetComparator {

    public static final SetComparator INSTANCE = new SetComparator();

    public CollectionChange compare(@NotNull Set<?> left, @NotNull Set<?> right, String propertyName) {
        CollectionChange collectionChange = new CollectionChange(propertyName);
        Sets.difference(left, right)
                .forEach(removed -> collectionChange.addValueRemoved(new ValueRemovedChange(propertyName, removed)));
        Sets.difference(right, left)
                .forEach(added -> collectionChange.addValueAdded(new ValueAddedChange(propertyName, added)));
        return collectionChange;
    }
}
