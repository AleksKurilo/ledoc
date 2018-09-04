package dk.ledocsystem.ledoc.annotations.validation.equipment;

import dk.ledocsystem.ledoc.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@RequiredArgsConstructor
class UniqueNameValidator implements ConstraintValidator<UniqueName, CharSequence> {

    private final EquipmentRepository equipmentRepository;

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        String tradeName = value.toString();
        return !equipmentRepository.existsByName(tradeName);
    }
}
