package dk.ledocsystem.ledoc.dto.equipment;

import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import dk.ledocsystem.ledoc.annotations.validation.equipment.UniqueName;
import dk.ledocsystem.ledoc.annotations.validation.review.ReviewDetails;
import dk.ledocsystem.ledoc.model.equipment.ApprovalType;
import dk.ledocsystem.ledoc.model.equipment.Status;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Getter
@Setter
@ReviewDetails
public class EquipmentEditDTO {

    @Size(min = 3, max = 40)
    @OnlyAscii
    @UniqueName
    private String name;

    @Size(min = 3, max = 40)
    @OnlyAscii
    private String idNumber;

    private Long authTypeId;

    private Long categoryId;

    private Long responsibleId;

    private Long locationId;

    private ApprovalType approvalType;

    private Period approvalRate;

    private Status status;

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
}
