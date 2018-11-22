package dk.ledocsystem.service.api.validation.review;

import dk.ledocsystem.service.api.dto.inbound.employee.EmployeeDetailsDTO;
import org.apache.commons.lang3.ObjectUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class ReviewDetailsValidatorForEmployee implements ConstraintValidator<ReviewDetails, EmployeeDetailsDTO> {

    @Override
    public boolean isValid(EmployeeDetailsDTO value, ConstraintValidatorContext context) {
        if (value.isSkillAssessed()) {
            return ObjectUtils.allNotNull(value.getSkillResponsibleId(), value.getReviewFrequency(), value.getReviewTemplateId());
        }
        return true;
    }
}
