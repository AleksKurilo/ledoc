package dk.ledocsystem.service.api.validation.document.category;

import dk.ledocsystem.data.repository.DocumentCategoryRepository;
import lombok.AllArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@AllArgsConstructor
class UniqueDocumentCategoryNameDaValidator implements ConstraintValidator<UniqueDocumentCategoryNameDa, CharSequence> {

    private final DocumentCategoryRepository categoryRepository;

    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String nameDa = value.toString();
        return !categoryRepository.existsByNameDa(nameDa);
    }

}
