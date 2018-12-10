package dk.ledocsystem.service.api.dto.inbound.document;

import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class DocumentSubcategoryDTO {

    private Long id;

    private Long categoryId;

    @Size(min = 3, max = 255)
    private String name;
}
