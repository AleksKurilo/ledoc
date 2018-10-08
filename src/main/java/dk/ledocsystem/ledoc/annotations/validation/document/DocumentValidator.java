package dk.ledocsystem.ledoc.annotations.validation.document;

import dk.ledocsystem.ledoc.dto.DocumentDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DocumentValidator implements ConstraintValidator<ValidDocument, DocumentDTO> {

    @Override
    public boolean isValid(DocumentDTO documentDTO, ConstraintValidatorContext constraintValidatorContext) {
        return (documentDTO.getEmployeeId() == null && documentDTO.getEquipmentId() == null) ? false : true;
    }
}
