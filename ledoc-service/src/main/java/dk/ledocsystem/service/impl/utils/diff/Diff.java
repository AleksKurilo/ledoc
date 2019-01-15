package dk.ledocsystem.service.impl.utils.diff;

import dk.ledocsystem.service.impl.utils.diff.changes.Change;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Diff {

    @Getter
    private List<Change> changes = new ArrayList<>();

    public void addChange(Change change) {
        changes.add(change);
    }

    public <C extends Change> List<C> getChangesByType(Class<C> changeClass) {
        return changes.stream()
                .filter(changeClass::isInstance)
                .map(changeClass::cast)
                .collect(Collectors.toList());
    }
}
