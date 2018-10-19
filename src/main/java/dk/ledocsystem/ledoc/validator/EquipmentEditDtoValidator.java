package dk.ledocsystem.ledoc.validator;

import dk.ledocsystem.ledoc.dto.equipment.EquipmentEditDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.equipment.Equipment;
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
public class EquipmentEditDtoValidator extends BaseValidator<EquipmentEditDTO> {

    private final EquipmentRepository equipmentRepository;

    @Override
    protected void validateUniqueProperty(EquipmentEditDTO dto, Map<String, List<String>> messages) {
        Equipment equipment = equipmentRepository.findById(dto.getId())
                .orElseThrow(() -> new NotFoundException(EQUIPMENT_ID_NOT_FOUND, dto.getId().toString()));
        String existName = equipment.getName();
        String newName = dto.getName();
        if (!existName.equals(newName) && equipmentRepository.existsByName(newName)) {
            messages.computeIfAbsent("name",
                    k -> new ArrayList<>()).add(this.messageSource.getMessage(EQUIPMENT_NAME_IS_ALREADY_IN_USE, null, LocaleContextHolder.getLocale()));
        }
    }
}