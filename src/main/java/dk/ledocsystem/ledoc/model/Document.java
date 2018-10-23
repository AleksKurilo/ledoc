package dk.ledocsystem.ledoc.model;

import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.model.equipment.Equipment;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDate;

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

    @Column(name = "create_on")
    private LocalDate createOn;

    @Column(name = "last_update")
    private LocalDate lastUpdate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    private Equipment equipment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Customer customer;

    @PreUpdate
    public void setLastUpdate() {
        lastUpdate = LocalDate.now();
    }
}
