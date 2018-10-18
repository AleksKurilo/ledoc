package dk.ledocsystem.ledoc.annotations.validation.review;

import dk.ledocsystem.ledoc.dto.employee.EmployeeDetailsDTO;
import lombok.Data;
import org.apache.commons.lang3.ObjectUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Data
public class ReviewDetailsValidatorForEmployee implements ConstraintValidator<ReviewDetails, EmployeeDetailsDTO> {

    @Override
    public boolean isValid(EmployeeDetailsDTO value, ConstraintValidatorContext context) {
        if (value.isSkillAssessed()) {
            return ObjectUtils.allNotNull(value.getSkillResponsibleId(), value.getReviewFrequency(), value.getReviewTemplateId());
        }
        return true;
    }
}
