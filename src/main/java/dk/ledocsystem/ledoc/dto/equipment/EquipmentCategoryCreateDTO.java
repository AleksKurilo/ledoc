package dk.ledocsystem.ledoc.dto.equipment;

import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import dk.ledocsystem.ledoc.annotations.validation.equipment.category.UniqueCategoryNameDa;
import dk.ledocsystem.ledoc.annotations.validation.equipment.category.UniqueCategoryNameEn;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EquipmentCategoryCreateDTO {

    @OnlyAscii
    @UniqueCategoryNameEn
    private String nameEn;

    @OnlyAscii
    @UniqueCategoryNameDa
    private String nameDa;

    private String reviewFrequency;
}
