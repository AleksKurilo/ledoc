package dk.ledocsystem.service.api.validation.supplier;

import dk.ledocsystem.data.repository.SupplierCategoryRepository;
import lombok.AllArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@AllArgsConstructor
class UniqueSupplierCategoryNameEnValidator implements ConstraintValidator<UniqueSupplierCategoryNameEn, CharSequence> {

    private final SupplierCategoryRepository categoryRepository;

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String nameEn = value.toString();
        return !categoryRepository.existsByNameEn(nameEn);
    }
}
