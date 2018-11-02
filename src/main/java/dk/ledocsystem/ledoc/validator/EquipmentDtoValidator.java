package dk.ledocsystem.ledoc.validator;

import dk.ledocsystem.ledoc.dto.equipment.EquipmentDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dk.ledocsystem.ledoc.constant.ErrorMessageKey.EQUIPMENT_ID_NOT_FOUND;
import static dk.ledocsystem.ledoc.constant.ErrorMessageKey.EQUIPMENT_NAME_IS_ALREADY_IN_USE;

@Component
@RequiredArgsConstructor
public class EquipmentDtoValidator extends BaseValidator<EquipmentDTO> {

    private final EquipmentRepository equipmentRepository;

    @Override
    protected void validateUniqueProperty(EquipmentDTO dto, Map<String, List<String>> messages) {
        String currentName = null;
        if (dto.getId() != null) {
            currentName = equipmentRepository.findById(dto.getId())
                    .orElseThrow(() -> new NotFoundException(EQUIPMENT_ID_NOT_FOUND, dto.getId().toString()))
                    .getName();
        }

        String newName = dto.getName();
        Long customerId = dto.getCustomerId();
        if (!newName.equals(currentName) && equipmentRepository.existsByNameAndCustomerId(newName, customerId)) {
            messages.computeIfAbsent("name",
                    k -> new ArrayList<>()).add(this.messageSource.getMessage(EQUIPMENT_NAME_IS_ALREADY_IN_USE, null, LocaleContextHolder.getLocale()));
        }
    }
}