package dk.ledocsystem.ledoc.annotations.validation.customer;

import dk.ledocsystem.ledoc.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
public class UniqueCVRValidator implements ConstraintValidator<UniqueCVR, CharSequence> {

    private final CustomerRepository customerRepository;

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String cvr = value.toString();
        return !customerRepository.findByCvr(cvr).isPresent();
    }
}
