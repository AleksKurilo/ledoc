package dk.ledocsystem.ledoc.validator;

import dk.ledocsystem.ledoc.dto.location.LocationDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dk.ledocsystem.ledoc.constant.ErrorMessageKey.LOCATION_ID_NOT_FOUND;
import static dk.ledocsystem.ledoc.constant.ErrorMessageKey.LOCATION_NAME_IS_ALREADY_IN_USE;

@Component
@RequiredArgsConstructor
public class LocationDtoValidator extends BaseValidator<LocationDTO> {

    private final LocationRepository locationRepository;

    @Override
    protected void validateUniqueProperty(LocationDTO dto, Map<String, List<String>> messages) {
        String currentName = null;
        if (dto.getId() != null) {
            currentName = locationRepository.findById(dto.getId())
                    .orElseThrow(() -> new NotFoundException(LOCATION_ID_NOT_FOUND, dto.getId().toString()))
                    .getName();
        }

        String newName = dto.getName();
        Long customerId = dto.getCustomerId();
        if (!newName.equals(currentName) && locationRepository.existsByNameAndCustomerId(newName, customerId)) {
            messages.computeIfAbsent("name",
                    k -> new ArrayList<>()).add(this.messageSource.getMessage(LOCATION_NAME_IS_ALREADY_IN_USE, null, LocaleContextHolder.getLocale()));
        }
    }
}