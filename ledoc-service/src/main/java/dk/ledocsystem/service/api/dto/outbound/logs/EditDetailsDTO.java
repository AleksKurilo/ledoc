package dk.ledocsystem.service.api.dto.outbound.logs;

import lombok.Value;

@Value
public class EditDetailsDTO {
    private String property;
    private String previousValue;
    private String currentValue;
}
