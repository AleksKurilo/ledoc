package dk.ledocsystem.service.api.dto.outbound.document;


import dk.ledocsystem.service.api.dto.inbound.document.DocumentCategoryDTO;
import lombok.Data;

@Data
public class GetDocumentSubcategoryDTO {

    private Long id;
    private String name;
    private DocumentCategoryDTO categoryDTO;
}
