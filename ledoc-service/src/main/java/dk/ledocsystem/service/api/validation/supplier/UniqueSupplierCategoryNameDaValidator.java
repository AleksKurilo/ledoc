package dk.ledocsystem.service.api.validation.supplier;

import dk.ledocsystem.data.repository.SupplierCategoryRepository;
import lombok.AllArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@AllArgsConstructor
class UniqueSupplierCategoryNameDaValidator implements ConstraintValidator<UniqueSupplierCategoryNameDa, CharSequence> {

    private final SupplierCategoryRepository categoryRepository;

    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String nameDa = value.toString();
        return !categoryRepository.existsByNameDa(nameDa);
    }

}
