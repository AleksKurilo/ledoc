package dk.ledocsystem.service.impl.utils.diff.changes;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class Change {

    @Getter
    private final String propertyName;
}
