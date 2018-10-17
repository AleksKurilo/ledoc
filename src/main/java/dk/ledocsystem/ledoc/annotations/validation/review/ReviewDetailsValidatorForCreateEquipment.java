package dk.ledocsystem.ledoc.annotations.validation.review;

import dk.ledocsystem.ledoc.dto.equipment.EquipmentCreateDTO;
import dk.ledocsystem.ledoc.model.equipment.ApprovalType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class ReviewDetailsValidatorForCreateEquipment
        implements ConstraintValidator<ReviewDetails, EquipmentCreateDTO> {

    @Override
    public boolean isValid(EquipmentCreateDTO value, ConstraintValidatorContext context) {
        return value.getApprovalType() != ApprovalType.ALL_TIME
                || (value.getApprovalRate() != null && value.getReviewTemplateId() != null);
    }
}
