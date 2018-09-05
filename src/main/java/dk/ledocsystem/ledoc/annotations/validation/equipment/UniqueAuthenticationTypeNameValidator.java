package dk.ledocsystem.ledoc.annotations.validation.equipment;

import dk.ledocsystem.ledoc.repository.AuthenticationTypeRepository;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
class UniqueAuthenticationTypeNameValidator implements ConstraintValidator<UniqueAuthenticationTypeName, CharSequence> {

    private final AuthenticationTypeRepository authenticationTypeRepository;

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String authTypeName = value.toString();
        return !authenticationTypeRepository.existsByNameEn(authTypeName);
    }
}
