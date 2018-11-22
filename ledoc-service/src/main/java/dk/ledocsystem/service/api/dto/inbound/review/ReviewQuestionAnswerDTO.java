package dk.ledocsystem.service.api.dto.inbound.review;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ReviewQuestionAnswerDTO {

    @NotNull
    private Long questionId;

    @NotNull
    @Size(max = 1000)
    private String answer;

    private String comment;
}
