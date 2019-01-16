package dk.ledocsystem.service.impl.utils.diff;

import dk.ledocsystem.data.model.logging.EditType;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SingleDiff {

    SingleDiff(String property, String prevValue, String curValue) {
        this(property, prevValue, curValue, EditType.VALUE_CHANGED);
    }

    private String property;
    private String previousValue;
    private String currentValue;
    private EditType editType;

}
