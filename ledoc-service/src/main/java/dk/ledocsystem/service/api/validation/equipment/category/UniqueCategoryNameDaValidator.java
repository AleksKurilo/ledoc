package dk.ledocsystem.service.api.validation.equipment.category;

import dk.ledocsystem.data.repository.EquipmentCategoryRepository;
import lombok.AllArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@AllArgsConstructor
class UniqueCategoryNameDaValidator implements ConstraintValidator<UniqueCategoryNameDa, CharSequence> {

    private final EquipmentCategoryRepository categoryRepository;

    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String nameDa = value.toString();
        return !categoryRepository.existsByNameDa(nameDa);
    }
}
