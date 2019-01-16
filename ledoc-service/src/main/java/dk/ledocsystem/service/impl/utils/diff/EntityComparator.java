package dk.ledocsystem.service.impl.utils.diff;

import dk.ledocsystem.service.impl.utils.diff.changes.ValueChange;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.Set;

public interface EntityComparator<T> {

    Diff compare(T left, T right);

    default void compareSimple(Object left, Object right, String propertyName, Diff diff) {
        if (!Objects.equals(left, right)) {
            diff.addChange(new ValueChange(propertyName, left, right));
        }
    }

    default void compareSets(Set<?> left, Set<?> right, String propertyName, Diff diff) {
        if (!Objects.equals(left, right)) {
            diff.addChange(new ValueChange(propertyName, StringUtils.join(left, ", "), StringUtils.join(right, ", ")));
        }
    }
}
