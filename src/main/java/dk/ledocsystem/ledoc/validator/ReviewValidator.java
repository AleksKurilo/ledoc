package dk.ledocsystem.ledoc.validator;

import dk.ledocsystem.ledoc.dto.review.ReviewDTO;
import dk.ledocsystem.ledoc.exceptions.ValidationDtoException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public class ReviewValidator extends BaseValidator<ReviewDTO> {

    @Override
    public void validate(ReviewDTO dto) {
        Map<String, List<String>> errors = getBasicValidation(dto);
        if (!errors.isEmpty()) {
            throw new ValidationDtoException(errors);
        }
    }
}
