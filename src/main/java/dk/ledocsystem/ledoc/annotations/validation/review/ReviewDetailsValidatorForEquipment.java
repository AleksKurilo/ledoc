package dk.ledocsystem.ledoc.annotations.validation.review;

import dk.ledocsystem.ledoc.dto.equipment.EquipmentDTO;
import dk.ledocsystem.ledoc.model.equipment.ApprovalType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class ReviewDetailsValidatorForEquipment implements ConstraintValidator<ReviewDetails, EquipmentDTO> {

    @Override
    public boolean isValid(EquipmentDTO value, ConstraintValidatorContext context) {
        return value.getApprovalType() == ApprovalType.NO_NEED
                || (value.getApprovalRate() != null && value.getReviewTemplateId() != null);
    }
}
