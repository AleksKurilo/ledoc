package dk.ledocsystem.ledoc.model.equipment;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import dk.ledocsystem.ledoc.model.Customer;
import dk.ledocsystem.ledoc.model.Location;
import dk.ledocsystem.ledoc.model.Supplier;
import dk.ledocsystem.ledoc.model.Visitable;
import dk.ledocsystem.ledoc.model.employee.Employee;
import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.Table;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "equipment", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "customer_id"})})
@ToString(of = {"id", "name"})
@DynamicInsert
@DynamicUpdate
public class Equipment implements Visitable {

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

    @Enumerated(EnumType.STRING)
    @Column(name = "approval_type", nullable = false)
    private ApprovalType approvalType;

    @Column(name = "approval_rate")
    private Period approvalRate;

    @Column(name = "next_review_date")
    private LocalDate nextReviewDate;

    @Column(length = 400)
    private String remark;

    @OneToOne(mappedBy = "equipment", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private EquipmentLoan loan;

    @ManyToMany
    @JoinTable(name = "equipment_log",
            joinColumns = { @JoinColumn(name = "equipment_id")},
            inverseJoinColumns = { @JoinColumn(name = "employee_id",
                    foreignKey = @ForeignKey(foreignKeyDefinition = "foreign key (employee_id) references employees on delete cascade")) })
    @OnDelete(action = OnDeleteAction.CASCADE)
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
     * To erase approval rate use {@link #eraseReviewDetails()}.
     * Setters methods are usually called from {@link dk.ledocsystem.ledoc.util.BeanCopyUtils}.
     */
    public void setApprovalRate(@NonNull Period approvalRate) {
        this.nextReviewDate = getPrevReviewDate().plus(approvalRate);
        this.approvalRate = approvalRate;
    }

    public void eraseReviewDetails() {
        this.approvalRate = null;
        this.nextReviewDate = null;
    }

    private LocalDate getPrevReviewDate() {
        return (nextReviewDate != null) ? nextReviewDate.minus(approvalRate) : LocalDate.now();
    }
}
