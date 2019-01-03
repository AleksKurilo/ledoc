package dk.ledocsystem.service.api.dto.outbound.document;

import lombok.Data;

@Data
public class GetFollowedDocumentDTO {

    private Long id;

    private String name;

    private boolean forced;
}
