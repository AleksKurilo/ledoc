package dk.ledocsystem.service.impl.utils.diff;

import lombok.Value;

@Value
public class SingleDiff {
    private String property;
    private String previousValue;
    private String currentValue;
}
