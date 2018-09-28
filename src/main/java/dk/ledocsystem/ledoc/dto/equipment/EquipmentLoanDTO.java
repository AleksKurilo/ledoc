package dk.ledocsystem.ledoc.dto.equipment;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Getter
@Setter
public class EquipmentLoanDTO {

    @NotNull
    private Long borrowerId;

    @NotNull
    private Long locationId;

    private boolean shouldBeInspected;

    private boolean borrowerResponsibleForReview;

    @FutureOrPresent
    private LocalDate deadline;

    private String comment;
}
