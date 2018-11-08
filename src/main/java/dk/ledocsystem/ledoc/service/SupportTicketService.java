package dk.ledocsystem.ledoc.service;

import dk.ledocsystem.ledoc.dto.SupportTicketDTO;
import dk.ledocsystem.ledoc.model.support_tickets.SupportTicket;

import java.util.List;

public interface SupportTicketService {

    List<SupportTicket> getAll();

    List<SupportTicket> getAllByEmployee(Long employeeId);

    /**
     * Send email to responsible person and create ticket
     *
     * @param supportTicketDTO Data required to reset password
     */
    void createSupportTicket(SupportTicketDTO supportTicketDTO);
}
