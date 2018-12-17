package dk.ledocsystem.service.impl.validators;

import dk.ledocsystem.data.repository.DocumentRepository;
import dk.ledocsystem.service.api.dto.inbound.document.DocumentDTO;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.DOCUMENT_ID_NOT_FOUND;
import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.DOCUMENT_NAME_IS_ALREADY_IN_USE;

@Component
@RequiredArgsConstructor
public class DocumentDtoValidator extends BaseValidator<DocumentDTO> {

    private final DocumentRepository documentRepository;

    @Override
    protected void validateInner(DocumentDTO dto, Map<String, Object> params, Map<String, List<String>> messages) {
        String currentName = null;
        if (dto.getId() != null) {
            currentName = documentRepository.findById(dto.getId())
                    .orElseThrow(() -> new NotFoundException(DOCUMENT_ID_NOT_FOUND, dto.getId().toString()))
                    .getName();
        }

        String newName = dto.getName();
        Long customerId = (Long) params.get("customerId");
        if (newName != null && !newName.equals(currentName) && documentRepository.existsByNameAndCustomerId(newName, customerId)) {
            messages.computeIfAbsent("name", k -> new ArrayList<>())
                    .add(this.messageSource.getMessage(DOCUMENT_NAME_IS_ALREADY_IN_USE, null, getLocale()));
        }
    }
}
