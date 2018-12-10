package dk.ledocsystem.service.api.dto.inbound.document;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class DocumentCategoryDTO {

    private Long id;

    @Size(min = 3, max = 255)
    private String name;
}
