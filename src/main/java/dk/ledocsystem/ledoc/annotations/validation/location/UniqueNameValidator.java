package dk.ledocsystem.ledoc.annotations.validation.location;

import dk.ledocsystem.ledoc.repository.LocationRepository;
import dk.ledocsystem.ledoc.service.CustomerService;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
class UniqueNameValidator implements ConstraintValidator<UniqueName, CharSequence> {

    private final CustomerService customerService;
    private final LocationRepository locationRepository;

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String name = value.toString();
        Long customerId = customerService.getCurrentCustomerReference().getId();
        return !locationRepository.existsByNameAndCustomerId(name, customerId);
    }
}
