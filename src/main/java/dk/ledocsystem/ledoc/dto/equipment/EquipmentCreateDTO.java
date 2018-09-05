package dk.ledocsystem.ledoc.dto.equipment;

import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import dk.ledocsystem.ledoc.annotations.validation.equipment.UniqueName;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
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
