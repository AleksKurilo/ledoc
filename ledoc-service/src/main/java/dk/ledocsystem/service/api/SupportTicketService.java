package dk.ledocsystem.service.api;

import dk.ledocsystem.service.api.dto.inbound.SupportTicketDTO;
import dk.ledocsystem.data.model.support_tickets.SupportTicket;

import java.util.List;

public interface SupportTicketService {

    List<SupportTicket> getAll();

    List<SupportTicket> getAllByEmployee(Long employeeId);

    /**
     * Send email to responsible person and create ticket
     *
     * @param supportTicketDTO Data required to create support ticket
     */
    void createSupportTicket(SupportTicketDTO supportTicketDTO);
}
