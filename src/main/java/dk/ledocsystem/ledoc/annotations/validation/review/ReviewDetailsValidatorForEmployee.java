package dk.ledocsystem.ledoc.annotations.validation.review;

import dk.ledocsystem.ledoc.dto.employee.EmployeeDetailsDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

class ReviewDetailsValidatorForEmployee implements ConstraintValidator<ReviewDetails, EmployeeDetailsDTO> {

    @Override
    public boolean isValid(EmployeeDetailsDTO value, ConstraintValidatorContext context) {
        if (value.isSkillAssessed()) {
            return value.getSkillResponsibleId() != null && value.getReviewFrequency() != null;
        }
        return true;
    }
}
