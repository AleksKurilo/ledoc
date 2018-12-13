package dk.ledocsystem.service.api.dto.inbound.document;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dk.ledocsystem.data.model.document.DocumentCategoryType;
import lombok.Data;

import javax.validation.constraints.Size;

@Data
public class DocumentCategoryDTO {

    private Long id;

    @Size(min = 3, max = 255)
    private String name;

    @JsonIgnore
    private DocumentCategoryType type;
}
