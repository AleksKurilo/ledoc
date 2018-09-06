package dk.ledocsystem.ledoc.dto.equipment;

import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import dk.ledocsystem.ledoc.annotations.validation.equipment.category.UniqueNameDa;
import dk.ledocsystem.ledoc.annotations.validation.equipment.category.UniqueNameEn;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentCategoryCreateDTO {

    @OnlyAscii
    @UniqueNameEn
    private String nameEn;

    @OnlyAscii
    @UniqueNameDa
    private String nameDa;

    private String reviewFrequency;
}
