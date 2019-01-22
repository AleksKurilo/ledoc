package dk.ledocsystem.service.impl.utils.diff;

import dk.ledocsystem.service.impl.utils.diff.changes.ValueChange;
import lombok.RequiredArgsConstructor;
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

        return result;
    }
}
