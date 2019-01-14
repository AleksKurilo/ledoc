package dk.ledocsystem.service.api.dto.inbound.supplier;

import dk.ledocsystem.service.api.validation.NonCyrillic;
import dk.ledocsystem.service.api.validation.PhoneNumber;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;
import java.util.Set;

@Getter
@Setter
public class SupplierDTO {

    private Long id;

    @NotNull
    private Long categoryId;

    @NotNull
    private Long responsibleId;

    @NotNull
    private Long reviewResponsible;

    @NotNull
    private Long reviewTemplateId;

    @NotNull
    private Set<Long> locationIds;

    @NotNull
    @Size(min = 3, max = 40)
    @NonCyrillic
    private String name;

    private String description;

    @PhoneNumber
    private String contactPhone;

    @Email
    private String contactEmail;

    @NotNull
    private boolean review;

    public Class<?>[] getValidationGroups() {
        return (review)
                ? new Class[]{MustBeReviewed.class, Default.class}
                : new Class[]{Default.class};
    }

    interface MustBeReviewed {
        // validation group marker interface
    }
}
