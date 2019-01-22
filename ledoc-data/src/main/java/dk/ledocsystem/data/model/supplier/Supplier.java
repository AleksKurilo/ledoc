package dk.ledocsystem.data.model.supplier;

import dk.ledocsystem.data.model.Customer;
import dk.ledocsystem.data.model.Location;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.review.ReviewTemplate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Period;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "suppliers",
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "customer_id"}))
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Supplier {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "supplier_seq")
    @SequenceGenerator(name = "supplier_seq", sequenceName = "supplier_seq")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "approval_rate")
    private Period approvalRate;

    @Column(name = "next_review_date")
    private LocalDate nextReviewDate;

    @ColumnDefault("false")
    @Column(nullable = false)
    private boolean archived;

    @Column(name = "archive_reason")
    private String archiveReason;

    @ColumnDefault("false")
    @Column(nullable = false)
    private boolean review;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private SupplierCategory category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "responsible_id", nullable = false)
    private Employee responsible;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_responsible_id", nullable = false)
    private Employee reviewResponsible;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private Employee creator;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_template_id")
    private ReviewTemplate reviewTemplate;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "supplier_location",
            joinColumns = {@JoinColumn(name = "supplier_id")},
            inverseJoinColumns = {@JoinColumn(name = "location_id")})
    private Set<Location> locations;

    /**
     * Automatically adjusts {@link #nextReviewDate} to the new value of approval rate.
     */
    public void setApprovalRate(Period approvalRate) {
        if (approvalRate == null) {
            eraseReviewDatails();
        } else {
            this.nextReviewDate = getPrevReviewDate().plus(approvalRate);
            this.approvalRate = approvalRate;
        }
    }

    public void eraseReviewDatails() {
        this.approvalRate = null;
        this.nextReviewDate = null;
    }

    private LocalDate getPrevReviewDate() {
        return (nextReviewDate != null) ? nextReviewDate.minus(approvalRate) : LocalDate.now();
    }

    @Override
    public String toString() {
        return name;
    }

}
