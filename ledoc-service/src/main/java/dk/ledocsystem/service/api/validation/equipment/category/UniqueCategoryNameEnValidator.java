package dk.ledocsystem.service.api.validation.equipment.category;


import dk.ledocsystem.data.repository.EquipmentCategoryRepository;
import lombok.AllArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@AllArgsConstructor
class UniqueCategoryNameEnValidator implements ConstraintValidator<UniqueCategoryNameEn, CharSequence> {

    private final EquipmentCategoryRepository categoryRepository;

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        /*if (value == null) {
            return true;
        }*/
        String nameEn = value.toString();
        return !categoryRepository.existsByNameEn(nameEn);
    }
}
