package dk.ledocsystem.ledoc.dto.review;

import lombok.Data;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Data
public class ReviewDTO {

    @Valid
    private List<ReviewQuestionAnswerDTO> answers = new ArrayList<>();

}
