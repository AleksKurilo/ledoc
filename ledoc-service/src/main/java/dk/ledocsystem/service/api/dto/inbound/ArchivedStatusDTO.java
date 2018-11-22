package dk.ledocsystem.service.api.dto.inbound;

import lombok.Data;

@Data
public class ArchivedStatusDTO {

    private boolean archived;

    private String archiveReason;
}
