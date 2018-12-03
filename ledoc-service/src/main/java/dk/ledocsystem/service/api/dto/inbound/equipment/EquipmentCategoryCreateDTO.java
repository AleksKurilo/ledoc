package dk.ledocsystem.service.api.dto.inbound.equipment;

import dk.ledocsystem.service.api.validation.NonCyrillic;
import dk.ledocsystem.service.api.validation.equipment.category.UniqueCategoryNameDa;
import dk.ledocsystem.service.api.validation.equipment.category.UniqueCategoryNameEn;
import lombok.Getter;
import lombok.Setter;

import java.time.Period;

@Getter
@Setter
public class EquipmentCategoryCreateDTO {

    @NonCyrillic
    @UniqueCategoryNameEn
    private String nameEn;

    @NonCyrillic
    @UniqueCategoryNameDa
    private String nameDa;

    private Period reviewFrequency;
}
