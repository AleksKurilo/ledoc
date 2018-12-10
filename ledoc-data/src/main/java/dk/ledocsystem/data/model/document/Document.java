package dk.ledocsystem.data.model.document;

import dk.ledocsystem.data.model.Customer;
import dk.ledocsystem.data.model.Location;
import dk.ledocsystem.data.model.Trade;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.equipment.Equipment;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Period;

@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name = "documents", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "customer_id"})})
public class Document {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "document_seq")
    @SequenceGenerator(name = "document_seq", sequenceName = "document_seq")
    private long id;

    @Column(nullable = false, length = 40)
    private String name;

    @Column(nullable = false)
    private String file;

    @Column(nullable = false)
    @ColumnDefault("false")
    private boolean archived;

    @Column(name = "archive_reason")
    private String archiveReason;

    @Column(name = "comment", length = 255)
    private String comment;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private DocumentType type;

    @Column(name = "source")
    @Enumerated(EnumType.STRING)
    private DocumentSource source;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private DocumentStatus status;

    @Column(name = "private")
    private boolean personal;

    @Column(name = "approval_rate")
    private Period approvalRate;

    @Column(name = "next_review_date")
    private LocalDate nextReviewDate;

    @Column(name = "create_on")
    private LocalDate createOn;

    @Column(name = "last_update")
    private LocalDate lastUpdate;

    @OneToOne(fetch = FetchType.LAZY)
    private Employee responsible;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    //@OnDelete(action = OnDeleteAction.CASCADE)
    private DocumentCategory category;

    @ManyToOne
    @JoinColumn(name = "subcategory_id", nullable = false)
    //@OnDelete(action = OnDeleteAction.CASCADE)
    private DocumentSubcategory subcategory;

    @ManyToOne(fetch = FetchType.LAZY)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    private Equipment equipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    private Trade trade;

    @PrePersist
    public void setreateOn() {
        this.createOn = LocalDate.now();
    }

    @PreUpdate
    public void setLastUpdate() {
        this.lastUpdate = LocalDate.now();
    }

    /**
     * Automatically adjusts {@link #nextReviewDate} to the new value of approval rate.
     */
    public void setApprovalRate(Period approvalRate) {
        if (approvalRate == null) {
            eraseReviewDetails();
        } else {
            this.nextReviewDate = getPrevReviewDate().plus(approvalRate);
            this.approvalRate = approvalRate;
        }
    }

    public void eraseReviewDetails() {
        this.approvalRate = null;
        this.nextReviewDate = null;
    }

    private LocalDate getPrevReviewDate() {
        return (nextReviewDate != null) ? nextReviewDate.minus(approvalRate) : LocalDate.now();
    }
}
