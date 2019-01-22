package dk.ledocsystem.service.api.dto.inbound.review;

import dk.ledocsystem.data.model.equipment.Status;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SimpleReviewDTO {

    @NotNull
    private Status status;
}
