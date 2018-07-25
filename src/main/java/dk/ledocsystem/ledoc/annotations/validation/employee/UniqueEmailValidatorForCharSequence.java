package dk.ledocsystem.ledoc.annotations.validation.employee;

import dk.ledocsystem.ledoc.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
class UniqueEmailValidatorForCharSequence implements ConstraintValidator<UniqueEmail, CharSequence> {

    private final EmployeeRepository employeeRepository;

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        String email = value.toString();
        return !employeeRepository.findByEmail(email).isPresent();
    }
}
