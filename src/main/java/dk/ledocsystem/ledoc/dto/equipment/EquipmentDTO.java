package dk.ledocsystem.ledoc.dto.equipment;

import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
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
public class EquipmentDTO {

    private Long id;
    private Long customerId; //todo should not be here

    @Size(min = 3, max = 40)
    @OnlyAscii
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
    private String comment;

    private String avatar;

    private boolean readyToLoan;

    private boolean reviewed;
}
