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
