package dk.ledocsystem.data.repository;

import dk.ledocsystem.data.model.support_tickets.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {

    List<SupportTicket> getAllByEmployee(Long employeeId);

}
