package dk.ledocsystem.service.api.validation.customer;

import dk.ledocsystem.data.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
class UniqueNameValidator implements ConstraintValidator<UniqueName, CharSequence> {

    private final CustomerRepository customerRepository;

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String name = value.toString();
        return !customerRepository.findByName(name).isPresent();
    }
}
