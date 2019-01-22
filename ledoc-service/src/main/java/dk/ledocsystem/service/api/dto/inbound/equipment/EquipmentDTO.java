package dk.ledocsystem.service.api.dto.inbound.equipment;

import dk.ledocsystem.data.model.equipment.ApprovalType;
import dk.ledocsystem.service.api.validation.NonCyrillic;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import javax.validation.groups.Default;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Getter
@Setter
public class EquipmentDTO {

    private Long id;

    @NotNull
    @Size(min = 3, max = 40)
    @NonCyrillic
    private String name;

    @Size(min = 3, max = 40)
    @NonCyrillic
    private String idNumber;

    private Long authTypeId;

    @NotNull
    private Long categoryId;

    @NotNull
    private Long responsibleId;

    @NotNull
    private Long locationId;

    @NotNull
    private ApprovalType approvalType;

    @NotNull(groups = MustBeSkillAssessed.class)
    private Period approvalRate;

    @NotNull(groups = MustBeSkillAssessed.class)
    private Long reviewTemplateId;

    @Size(min = 3, max = 40)
    @NonCyrillic
    private String manufacturer;

    @PastOrPresent
    private LocalDate purchaseDate = LocalDate.now();

    @Size(min = 3, max = 40)
    @NonCyrillic
    private String serialNumber;

    @Size(min = 3, max = 40)
    @NonCyrillic
    private String localId;

    @FutureOrPresent
    private LocalDate warrantyDate;

    @PositiveOrZero
    private BigDecimal price;

    @Size(max = 400)
    @NonCyrillic
    private String comment;

    private String avatar;

    private boolean readyToLoan;

    private boolean reviewed;

    interface MustBeSkillAssessed {
        // validation group marker interface
    }

    public Class<?>[] getValidationGroups() {
        return (approvalType == ApprovalType.NO_NEED)
                ? new Class[] {Default.class}
                : new Class[] {MustBeSkillAssessed.class, Default.class};
    }
}
