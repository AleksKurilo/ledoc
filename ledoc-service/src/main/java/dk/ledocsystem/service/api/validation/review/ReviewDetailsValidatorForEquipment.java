package dk.ledocsystem.service.api.validation.review;

import dk.ledocsystem.service.api.dto.inbound.equipment.EquipmentDTO;
import dk.ledocsystem.data.model.equipment.ApprovalType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class ReviewDetailsValidatorForEquipment implements ConstraintValidator<ReviewDetails, EquipmentDTO> {

    @Override
    public boolean isValid(EquipmentDTO value, ConstraintValidatorContext context) {
        return value.getApprovalType() == ApprovalType.NO_NEED
                || (value.getApprovalRate() != null && value.getReviewTemplateId() != null);
    }
}
