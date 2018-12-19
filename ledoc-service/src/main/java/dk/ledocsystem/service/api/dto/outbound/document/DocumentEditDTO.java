package dk.ledocsystem.service.api.dto.outbound.document;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class DocumentEditDTO extends GetDocumentDTO {

    private Long categoryId;

    private Long subcategoryId;

    private Long responsibleId;

    private String file;

    private Set<Long> locationIds;

    private Set<Long> tradeIds;
}
