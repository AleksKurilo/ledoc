package dk.ledocsystem.data.model.logging;

import dk.ledocsystem.data.model.employee.Employee;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@DynamicInsert
@MappedSuperclass
public abstract class AbstractLog {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "log_seq")
    @SequenceGenerator(name = "log_seq", sequenceName = "log_seq")
    private Long id;

    @Basic(optional = false)
    @Column(nullable = false, insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(name="type", nullable = false)
    private LogType logType;

    public boolean isEditLog() {
        return logType == LogType.Edit;
    }
}
