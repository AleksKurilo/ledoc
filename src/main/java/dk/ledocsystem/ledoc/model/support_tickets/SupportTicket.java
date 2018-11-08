package dk.ledocsystem.ledoc.model.support_tickets;

import dk.ledocsystem.ledoc.model.employee.Employee;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Setter
@Getter
@Entity
@Table(name = "support_tickets")
public class SupportTicket {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "support_ticket_seq")
    @SequenceGenerator(name = "support_ticket_seq", sequenceName = "support_ticket_seq")
    private Long id;

    @Basic(optional = false)
    @Column(updatable = false)
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
