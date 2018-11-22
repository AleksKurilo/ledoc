package dk.ledocsystem.data.model.review;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

@Getter
@Setter
@MappedSuperclass
public abstract class ReviewQuestionAnswer {

    public abstract Long getId();

    @Column(nullable = false, length = 1000)
    private String answer;

    @Column(length = 500)
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ReviewQuestion reviewQuestion;

    public abstract<T extends Review> T getReview();
}
