package dk.ledocsystem.ledoc.dto;

import lombok.Data;

@Data
public class ArchivedStatusDTO {

    private boolean archived;

    private String archiveReason;
}
