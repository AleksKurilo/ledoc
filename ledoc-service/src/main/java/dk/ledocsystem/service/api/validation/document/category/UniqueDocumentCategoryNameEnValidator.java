package dk.ledocsystem.service.api.validation.document.category;

import dk.ledocsystem.data.repository.DocumentCategoryRepository;
import lombok.AllArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@AllArgsConstructor
class UniqueDocumentCategoryNameEnValidator implements ConstraintValidator<UniqueDocumentCategoryNameEn, CharSequence> {

    private final DocumentCategoryRepository categoryRepository;

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String nameEn = value.toString();
        return !categoryRepository.existsByNameEn(nameEn);
    }
}
