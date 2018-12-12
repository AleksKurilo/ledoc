package dk.ledocsystem.data.model.equipment;

import dk.ledocsystem.data.model.Customer;
import dk.ledocsystem.data.model.Location;
import dk.ledocsystem.data.model.Supplier;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.model.review.ReviewTemplate;
import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Iterator;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "equipment", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "customer_id"})})
@ToString(of = {"id", "name"})
@DynamicInsert
@DynamicUpdate
public class Equipment {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "equipment_seq")
    @SequenceGenerator(name = "equipment_seq", sequenceName = "equipment_seq")
    private Long id;

    @Column(nullable = false, length = 40)
    private String name;

    @Column(name = "id_number", length = 40)
    private String idNumber;

    @Column(name = "serial_number", length = 40)
    private String serialNumber;

    @Column(name = "local_id", length = 40)
    private String localId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private EquipmentCategory category;

    @Column(length = 40)
    private String manufacturer;

    @Column
    private BigDecimal price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    @ColumnDefault("\'OK\'")
    private Status status;

    @Column(name = "purchase_date", nullable = false)
    private LocalDate purchaseDate;

    @Column(name = "warranty_date")
    private LocalDate warrantyDate;

    @ColumnDefault("false")
    @Column(nullable = false)
    private Boolean archived;

    @Column(name = "archive_reason")
    private String archiveReason;

    @Column(name = "avatar")
    private String avatar;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_type_id")
    private AuthenticationType authenticationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private Employee creator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_id", nullable = false)
    private Employee responsible;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_template_id")
    private ReviewTemplate reviewTemplate;

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_type", nullable = false)
    private ApprovalType approvalType;

    @Column(name = "approval_rate")
    private Period approvalRate;

    @Column(name = "next_review_date")
    private LocalDate nextReviewDate;

    @Column(length = 400)
    private String comment;

    @ColumnDefault("false")
    @Column(name = "ready_to_loan", nullable = false)
    private Boolean readyToLoan;

    @OneToOne(mappedBy = "equipment", cascade = CascadeType.ALL, orphanRemoval = true)
    private EquipmentLoan loan;

    @OneToMany(mappedBy = "equipment", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private Set<FollowedEquipment> followedEquipments;

    public boolean isLoaned() {
        return loan != null;
    }

    @ManyToMany
    @JoinTable(name = "equipment_logs",
            joinColumns = { @JoinColumn(name = "equipment_id")},
            inverseJoinColumns = { @JoinColumn(name = "employee_id",
                    foreignKey = @ForeignKey(foreignKeyDefinition = "foreign key (employee_id) references employees on delete cascade")) })
    @OnDelete(action = OnDeleteAction.CASCADE)
    @WhereJoinTable(clause = "type = 'Read' OR type = 'Archive'")
    private Set<Employee> visitedBy;

    public void setLoan(@NonNull EquipmentLoan equipmentLoan) {
        this.loan = equipmentLoan;
        equipmentLoan.setEquipment(this);
    }

    public void removeLoan() {
        if (this.loan != null) {
            loan.setEquipment(null);
        }
        this.loan = null;
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
    public void addFollower(Employee employee, boolean forced) {
        FollowedEquipment followedEquipment = new FollowedEquipment(employee, this, forced);
        followedEquipments.add(followedEquipment);
        employee.getFollowedEquipments().add(followedEquipment);
    }

    public void removeFollower(Employee employee) {
        for (Iterator<FollowedEquipment> iterator = followedEquipments.iterator();
             iterator.hasNext(); ) {
            FollowedEquipment followedEquipment = iterator.next();

            if (followedEquipment.getEquipment().equals(this) &&
                    followedEquipment.getEmployee().equals(employee)) {
                iterator.remove();
                followedEquipment.getEmployee().getFollowedEquipments().remove(followedEquipment);
            }
        }
    }
}
