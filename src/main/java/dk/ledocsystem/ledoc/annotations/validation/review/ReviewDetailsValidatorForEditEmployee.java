package dk.ledocsystem.ledoc.annotations.validation.review;

import dk.ledocsystem.ledoc.dto.employee.EmployeeDetailsEditDTO;
import org.apache.commons.lang3.BooleanUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class ReviewDetailsValidatorForEditEmployee
        implements ConstraintValidator<ReviewDetails, EmployeeDetailsEditDTO> {

    @Override
    public boolean isValid(EmployeeDetailsEditDTO value, ConstraintValidatorContext context) {
        if (BooleanUtils.isTrue(value.getSkillAssessed())) {
            return value.getSkillResponsibleId() != null && value.getReviewFrequency() != null;
        }
        return true;
    }
}
