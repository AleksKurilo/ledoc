package dk.ledocsystem.ledoc.annotations.validation.review;

import dk.ledocsystem.ledoc.dto.equipment.EquipmentEditDTO;
import dk.ledocsystem.ledoc.model.equipment.ApprovalType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class ReviewDetailsValidatorForEditEquipment
        implements ConstraintValidator<ReviewDetails, EquipmentEditDTO> {

    @Override
    public boolean isValid(EquipmentEditDTO value, ConstraintValidatorContext context) {
        return value.getApprovalType() != ApprovalType.ALL_TIME
                || (value.getApprovalRate() != null && value.getReviewTemplateId() != null);
    }
}
