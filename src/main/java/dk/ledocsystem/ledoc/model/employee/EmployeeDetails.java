package dk.ledocsystem.ledoc.model.employee;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Period;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class EmployeeDetails {

    @Column(length = 400)
    private String comment;

    @Column(name = "review_frequency")
    private Period reviewFrequency;

    @Column(name = "next_review_date")
    private LocalDate nextReviewDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_of_skills_id")
    private Employee responsibleOfSkills;

    /**
     * Automatically adjusts {@link #nextReviewDate} to the new value of review frequency.
     * To erase review frequency use {@link #eraseReviewDetails()}.
     * Setters methods are usually called from {@link dk.ledocsystem.ledoc.util.BeanCopyUtils}.
     */
    public void setReviewFrequency(@NonNull Period reviewFrequency) {
        this.nextReviewDate = getPrevReviewDate().plus(reviewFrequency);
        this.reviewFrequency = reviewFrequency;
    }

    public void eraseReviewDetails() {
        this.reviewFrequency = null;
        this.nextReviewDate = null;
        this.responsibleOfSkills = null;
    }

    private LocalDate getPrevReviewDate() {
        return (nextReviewDate != null) ? nextReviewDate.minus(reviewFrequency) : LocalDate.now();
    }
}
