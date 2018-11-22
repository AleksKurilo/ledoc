package dk.ledocsystem.data.model.review;

import dk.ledocsystem.data.model.employee.Employee;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import java.util.List;

/**
 * @param <T> Type of the review subject
 */
@Getter
@Setter
@MappedSuperclass
public abstract class Review<T> {

    public abstract Long getId();

    @ManyToOne(fetch = FetchType.LAZY)
    private Employee reviewer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ReviewTemplate reviewTemplate;

    public abstract T getSubject();

    public abstract<QType extends ReviewQuestionAnswer> List<QType> getAnswers();
}
