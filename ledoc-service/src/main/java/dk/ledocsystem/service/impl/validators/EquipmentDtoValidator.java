package dk.ledocsystem.service.impl.validators;

import dk.ledocsystem.service.api.dto.inbound.equipment.EquipmentDTO;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import dk.ledocsystem.data.repository.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.EQUIPMENT_ID_NOT_FOUND;
import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.EQUIPMENT_NAME_IS_ALREADY_IN_USE;

@Component
@RequiredArgsConstructor
class EquipmentDtoValidator extends BaseValidator<EquipmentDTO> {

    private final EquipmentRepository equipmentRepository;

    @Override
    protected void validateInner(EquipmentDTO dto, Map<String, Object> params, Map<String, List<String>> messages) {
        String currentName = null;
        if (dto.getId() != null) {
            currentName = equipmentRepository.findById(dto.getId())
                    .orElseThrow(() -> new NotFoundException(EQUIPMENT_ID_NOT_FOUND, dto.getId().toString()))
                    .getName();
        }

        String newName = dto.getName();
        Long customerId = (Long) params.get("customerId");
        if (newName != null && !newName.equals(currentName) && equipmentRepository.existsByNameAndCustomerId(newName, customerId)) {
            messages.computeIfAbsent("name", k -> new ArrayList<>())
                    .add(this.messageSource.getMessage(EQUIPMENT_NAME_IS_ALREADY_IN_USE, null, getLocale()));
        }
    }
}