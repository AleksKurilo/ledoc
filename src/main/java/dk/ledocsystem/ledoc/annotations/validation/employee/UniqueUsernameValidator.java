package dk.ledocsystem.ledoc.annotations.validation.employee;

import dk.ledocsystem.ledoc.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, CharSequence> {

    private final EmployeeRepository employeeRepository;

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String username = value.toString();
        return !employeeRepository.findByUsername(username).isPresent();
    }
}
