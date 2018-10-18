package dk.ledocsystem.ledoc.dto.equipment;

import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import dk.ledocsystem.ledoc.annotations.validation.equipment.UniqueName;
import dk.ledocsystem.ledoc.annotations.validation.review.ReviewDetails;
import dk.ledocsystem.ledoc.model.equipment.ApprovalType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Getter
@Setter
@ReviewDetails
public class EquipmentCreateDTO {

    @NotNull
    @Size(min = 3, max = 40)
    @OnlyAscii
    @UniqueName
    private String name;

    @Size(min = 3, max = 40)
    @OnlyAscii
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

    private Period approvalRate;

    private Long reviewTemplateId;

    @Size(min = 3, max = 40)
    @OnlyAscii
    private String manufacturer;

    @PastOrPresent
    private LocalDate purchaseDate = LocalDate.now();

    @Size(min = 3, max = 40)
    @OnlyAscii
    private String serialNumber;

    @Size(min = 3, max = 40)
    @OnlyAscii
    private String localId;

    @FutureOrPresent
    private LocalDate warrantyDate;

    @PositiveOrZero
    private BigDecimal price;

    @Size(max = 400)
    @OnlyAscii
    private String remark;

    private String avatar;
}
