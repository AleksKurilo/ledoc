package dk.ledocsystem.service.api.dto.outbound.logs;

import dk.ledocsystem.data.model.logging.EditType;
import lombok.Value;

@Value
public class EditDetailsDTO {
    private String property;
    private String previousValue;
    private String currentValue;
    private EditType editType;
}
