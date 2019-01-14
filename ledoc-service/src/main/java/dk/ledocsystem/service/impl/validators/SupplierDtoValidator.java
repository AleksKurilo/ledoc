package dk.ledocsystem.service.impl.validators;

import dk.ledocsystem.data.repository.SupplierRepository;
import dk.ledocsystem.service.api.dto.inbound.equipment.EquipmentDTO;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.SUPPLIER_ID_NOT_FOUND;
import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.SUPPLIER_NAME_IS_ALREADY_IN_USE;

@Component
@RequiredArgsConstructor
public class SupplierDtoValidator extends BaseValidator<EquipmentDTO> {

    private final SupplierRepository supplierRepository;

    @Override
    protected void validateInner(EquipmentDTO dto, Map<String, Object> params, Map<String, List<String>> messages) {
        String currentName = null;
        if (dto.getId() != null) {
            currentName = supplierRepository.findById(dto.getId())
                    .orElseThrow(() -> new NotFoundException(SUPPLIER_ID_NOT_FOUND, dto.getId().toString()))
                    .getName();
        }

        String newName = dto.getName();
        Long customerId = (Long) params.get("customerId");
        if (newName != null && !newName.equals(currentName) && supplierRepository.existsByNameAndCustomerId(newName, customerId)) {
            messages.computeIfAbsent("name", k -> new ArrayList<>())
                    .add(this.messageSource.getMessage(SUPPLIER_NAME_IS_ALREADY_IN_USE, null, getLocale()));
        }
    }
}
