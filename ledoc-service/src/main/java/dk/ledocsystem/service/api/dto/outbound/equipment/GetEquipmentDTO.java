package dk.ledocsystem.service.api.dto.outbound.equipment;

import dk.ledocsystem.data.model.equipment.Status;
import lombok.Data;

@Data
public class GetEquipmentDTO {

    private Long id;

    private String name;

    private String location;

    private Status status;

    private String localId;

    private String avatar;

    private String borrowerAvatar;

    private boolean readyToLoan;

    private boolean loaned;
}
