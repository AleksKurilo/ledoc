package dk.ledocsystem.service.impl.utils.diff;

import dk.ledocsystem.data.model.logging.EditType;
import dk.ledocsystem.service.impl.utils.diff.changes.CollectionChange;
import dk.ledocsystem.service.impl.utils.diff.changes.ValueChange;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class DiffFinder {

    public <T> List<SingleDiff> findDiff(T left, T right, EntityComparator<? super T> comparator) {
        Diff diff = comparator.compare(left, right);
        List<SingleDiff> result = new ArrayList<>();

        for (ValueChange valueChange : diff.getChangesByType(ValueChange.class)) {
            String propertyName = valueChange.getPropertyName();
            Object leftValue = valueChange.getLeft();
            Object rightValue = valueChange.getRight();
            result.add(new SingleDiff(propertyName, Objects.toString(leftValue, null), Objects.toString(rightValue, null)));
        }

        for (CollectionChange collectionChange : diff.getChangesByType(CollectionChange.class)) {
            String propertyName = collectionChange.getPropertyName();
            List<?> addedValues = collectionChange.getAddedValues();
            if (!addedValues.isEmpty()) {
                result.add(new SingleDiff(propertyName, null, joinList(addedValues), EditType.ADDED));
            }
            List<?> removedValues = collectionChange.getRemovedValues();
            if (!removedValues.isEmpty()) {
                result.add(new SingleDiff(propertyName, null, joinList(removedValues), EditType.REMOVED));
            }
        }

        return result;
    }

    private String joinList(List<?> list) {
        return StringUtils.join(list, ", ");
    }
}
