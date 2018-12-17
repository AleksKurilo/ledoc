package dk.ledocsystem.service.impl.validators;

import dk.ledocsystem.service.api.dto.inbound.location.LocationDTO;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import dk.ledocsystem.data.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.LOCATION_ID_NOT_FOUND;
import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.LOCATION_NAME_IS_ALREADY_IN_USE;

@Component
@RequiredArgsConstructor
class LocationDtoValidator extends BaseValidator<LocationDTO> {

    private final LocationRepository locationRepository;

    @Override
    protected void validateInner(LocationDTO dto, Map<String, Object> params, Map<String, List<String>> messages) {
        String currentName = null;
        if (dto.getId() != null) {
            currentName = locationRepository.findById(dto.getId())
                    .orElseThrow(() -> new NotFoundException(LOCATION_ID_NOT_FOUND, dto.getId().toString()))
                    .getName();
        }

        String newName = dto.getName();
        Long customerId = (Long) params.get("customerId");
        if (newName != null && !newName.equals(currentName) && locationRepository.existsByNameAndCustomerId(newName, customerId)) {
            messages.computeIfAbsent("name", k -> new ArrayList<>())
                    .add(this.messageSource.getMessage(LOCATION_NAME_IS_ALREADY_IN_USE, null, getLocale()));
        }
    }
}