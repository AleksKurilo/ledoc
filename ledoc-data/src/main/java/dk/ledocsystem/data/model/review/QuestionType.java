package dk.ledocsystem.data.model.review;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum QuestionType {
    YES_NO("Yes/No"), RANKING("ranking");

    private final String value;

    @JsonValue
    public String value() {
        return value;
    }
}
