package dk.ledocsystem.service.api.validation.document;

import dk.ledocsystem.service.api.dto.inbound.document.DocumentDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class DocumentValidator implements ConstraintValidator<ValidDocument, DocumentDTO> {

    @Override
    public boolean isValid(DocumentDTO documentDTO, ConstraintValidatorContext constraintValidatorContext) {
        return (documentDTO.getEmployeeId() != null || documentDTO.getEquipmentId() != null);
    }
}
