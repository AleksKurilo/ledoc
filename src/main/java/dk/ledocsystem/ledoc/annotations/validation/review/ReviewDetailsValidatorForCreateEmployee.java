package dk.ledocsystem.ledoc.annotations.validation.review;

import dk.ledocsystem.ledoc.dto.employee.EmployeeDetailsCreateDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class ReviewDetailsValidatorForCreateEmployee
        implements ConstraintValidator<ReviewDetails, EmployeeDetailsCreateDTO> {

    @Override
    public boolean isValid(EmployeeDetailsCreateDTO value, ConstraintValidatorContext context) {
        if (value.isSkillAssessed()) {
            return value.getSkillResponsibleId() != null && value.getReviewFrequency() != null;
        }
        return true;
    }
}
