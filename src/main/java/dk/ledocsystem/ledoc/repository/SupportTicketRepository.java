package dk.ledocsystem.ledoc.repository;

import dk.ledocsystem.ledoc.model.support_tickets.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {

    List<SupportTicket> getAllByEmployee(Long employeeId);

}
