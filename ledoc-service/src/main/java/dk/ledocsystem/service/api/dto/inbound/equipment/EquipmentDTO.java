package dk.ledocsystem.service.api.dto.inbound.equipment;

import dk.ledocsystem.service.api.validation.NonCyrillic;
import dk.ledocsystem.service.api.validation.review.ReviewDetails;
import dk.ledocsystem.data.model.equipment.ApprovalType;
import dk.ledocsystem.data.model.equipment.Status;
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

    private Period approvalRate;

    private Long reviewTemplateId;

    private Status status;

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
}
