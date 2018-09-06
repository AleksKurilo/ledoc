package dk.ledocsystem.ledoc.model.equipment;

import dk.ledocsystem.ledoc.exceptions.InvalidReviewFrequency;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
public enum ReviewFrequency {
    MONTH_1("1 month"),
    MONTH_2("2 months"),
    MONTH_3("3 months"),
    MONTH_6("6 months"),
    MONTH_12("12 months"),
    MONTH_18("18 month"),
    MONTH_24("24 months"),
    MONTH_36("36 months"),
    OTHER("other");

    @Getter
    private final String periodValue;

    public static ReviewFrequency fromString(String frequency) {
        for (ReviewFrequency reviewFrequency : values()) {
            if (Objects.equals(reviewFrequency.periodValue, frequency))
                return reviewFrequency;
        }
        throw new InvalidReviewFrequency("equipment.review.frequency.invalid", frequency);
    }
}

