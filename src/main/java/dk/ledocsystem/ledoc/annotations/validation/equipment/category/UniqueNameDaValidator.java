package dk.ledocsystem.ledoc.annotations.validation.equipment.category;

import dk.ledocsystem.ledoc.repository.EquipmentCategoryRepository;
import lombok.AllArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@AllArgsConstructor
public class UniqueNameDaValidator implements ConstraintValidator<UniqueCategoryNameDa, CharSequence> {

    private final EquipmentCategoryRepository categoryRepository;

    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String nameDa = value.toString();
        return !categoryRepository.existsByNameDa(nameDa);
    }
}
