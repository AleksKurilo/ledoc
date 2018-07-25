package dk.ledocsystem.ledoc.annotations.validation.customer;

import dk.ledocsystem.ledoc.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class UniqueNameValidator implements ConstraintValidator<UniqueName, CharSequence> {

    private final CustomerRepository customerRepository;

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        String name = value.toString();
        return !customerRepository.findByName(name).isPresent();
    }
}
