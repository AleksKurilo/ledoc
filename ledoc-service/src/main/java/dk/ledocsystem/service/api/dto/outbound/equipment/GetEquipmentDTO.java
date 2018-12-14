package dk.ledocsystem.service.api.dto.outbound.equipment;

import dk.ledocsystem.data.model.equipment.Status;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;

@Data
public class GetEquipmentDTO {

    private Long id;

    private String name;

    private String location;

    private Status status;

    private String idNumber;

    private String localId;

    private String serialNumber;

    private String category;

    private String responsible;

    private Period approvalRate;

    private String manufacturer;

    private LocalDate purchaseDate;

    private LocalDate warrantyDate;

    private String authenticationType;

    private BigDecimal price;

    private String comment;

    private String avatar;

    private String borrowerAvatar;

    private boolean readyToLoan;

    private boolean loaned;
}
