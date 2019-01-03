package dk.ledocsystem.service.api.dto.outbound.document;

import lombok.Data;

@Data
public class EmployeeByDocumentReadStatusDTO {

    private String name;
    private boolean read;
}
