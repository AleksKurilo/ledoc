package dk.ledocsystem.ledoc.annotations.validation.equipment.category;


import dk.ledocsystem.ledoc.repository.EquipmentCategoryRepository;
import lombok.AllArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@AllArgsConstructor
public class UniqueNameEnValidator implements ConstraintValidator<UniqueNameEn, CharSequence> {

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
