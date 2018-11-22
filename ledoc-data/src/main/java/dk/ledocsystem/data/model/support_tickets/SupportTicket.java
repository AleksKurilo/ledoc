package dk.ledocsystem.data.model.support_tickets;

import dk.ledocsystem.data.model.employee.Employee;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "support_tickets")
public class SupportTicket {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "support_ticket_seq")
    @SequenceGenerator(name = "support_ticket_seq", sequenceName = "support_ticket_seq")
    private Long id;

    @Basic(optional = false)
    @Column(insertable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "theme", length = 40)
    private String theme;

    @Column(name = "message")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name="page_location", nullable = false)
    private PageLocation pageLocation;
}
