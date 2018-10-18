package dk.ledocsystem.ledoc.model.employee;

import dk.ledocsystem.ledoc.model.review.ReviewTemplate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_template_id")
    private ReviewTemplate reviewTemplate;

    public boolean getSkillAssessed() {
        return nextReviewDate != null;
    }

    /**
     * Automatically adjusts {@link #nextReviewDate} to the new value of review frequency.
     */
    public void setReviewFrequency(Period reviewFrequency) {
        if (reviewFrequency == null) {
            eraseReviewDetails();
        } else {
            this.nextReviewDate = getPrevReviewDate().plus(reviewFrequency);
            this.reviewFrequency = reviewFrequency;
        }
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
