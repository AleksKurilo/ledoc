package dk.ledocsystem.service.api.dto.inbound.document;

import dk.ledocsystem.data.model.document.DocumentSource;
import dk.ledocsystem.data.model.document.DocumentStatus;
import dk.ledocsystem.data.model.document.DocumentType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;
import java.time.Period;
import java.util.Set;

@Setter
@Getter
public class DocumentDTO {

    private Long id;

    @NotNull
    @Size(min = 2, max = 40)
    private String name;

    @NotNull
    private String file;

    @Size(min = 4, max = 40)
    private String idNumber;

    @Size(max = 255)
    private String comment;

    @NotNull
    private DocumentType type;

    @NotNull
    private DocumentSource source;

    @NotNull
    private DocumentStatus status;

    @NotNull(groups = MustBeReviewed.class)
    private Period approvalRate;

    private boolean personal;

    @NotNull
    private Long responsibleId;

    @NotNull
    private Long categoryId;

    @NotNull
    private Long subcategoryId;

    @NotEmpty
    private Set<Long> locationIds;

    @NotEmpty
    private Set<Long> tradeIds;

    interface MustBeReviewed {
        // validation group marker interface
    }

    public Class<?>[] getValidationGroups() {
        return (status == DocumentStatus.ACTIVE_WITH_REVIEW)
                ? new Class[] {MustBeReviewed.class, Default.class}
                : new Class[] {Default.class};
    }

}
