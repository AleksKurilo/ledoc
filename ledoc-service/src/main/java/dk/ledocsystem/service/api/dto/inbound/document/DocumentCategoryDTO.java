package dk.ledocsystem.service.api.dto.inbound.document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dk.ledocsystem.data.model.document.DocumentCategoryType;
import dk.ledocsystem.service.api.validation.NonCyrillic;
import dk.ledocsystem.service.api.validation.document.category.UniqueDocumentCategoryNameDa;
import dk.ledocsystem.service.api.validation.document.category.UniqueDocumentCategoryNameEn;
import lombok.Data;

@Data
public class DocumentCategoryDTO {

    private Long id;

    @NonCyrillic
    @UniqueDocumentCategoryNameEn
    private String nameEn;

    @NonCyrillic
    @UniqueDocumentCategoryNameDa
    private String nameDa;

    @JsonIgnore
    private DocumentCategoryType type;
}
