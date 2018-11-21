package dk.ledocsystem.ledoc.service.impl;

import com.google.common.collect.ImmutableMap;
import dk.ledocsystem.ledoc.dto.SupportTicketDTO;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.email_notifications.EmailNotification;
import dk.ledocsystem.ledoc.model.support_tickets.SupportTicket;
import dk.ledocsystem.ledoc.repository.EmailNotificationRepository;
import dk.ledocsystem.ledoc.repository.EmployeeRepository;
import dk.ledocsystem.ledoc.repository.SupportTicketRepository;
import dk.ledocsystem.ledoc.service.SupportTicketService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static dk.ledocsystem.ledoc.constant.ErrorMessageKey.EMPLOYEE_ID_NOT_FOUND;

@Service
@AllArgsConstructor
class SupportTicketServiceImpl implements SupportTicketService {

    private final EmailNotificationRepository emailNotificationRepository;

    private final SupportTicketRepository supportTicketRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public List<SupportTicket> getAll() {
        return supportTicketRepository.findAll();
    }

    @Override
    public List<SupportTicket> getAllByEmployee(@NonNull Long employeeId) {
        return supportTicketRepository.getAllByEmployee(employeeId);
    }

    @Transactional
    @Override
    public void createSupportTicket(SupportTicketDTO supportTicketDTO) {
        SupportTicket ticket = new SupportTicket();
        ticket.setCreated(new Date());
        ticket.setEmployee(employeeRepository.findById(supportTicketDTO.getEmployeeId())
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_ID_NOT_FOUND, supportTicketDTO.getEmployeeId().toString())));
        ticket.setTheme(supportTicketDTO.getTheme());
        ticket.setMessage(supportTicketDTO.getMessage());
        ticket.setPageLocation(supportTicketDTO.getPageLocation());
        ticket = supportTicketRepository.save(ticket);

        sendNotificationToResponsible(ticket,"testmytests43@gmail.com");
    }

    private void sendNotificationToResponsible(SupportTicket ticket, String email) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        Map<String, Object> model = ImmutableMap.<String, Object>builder()
                .put("created", sdf.format(ticket.getCreated()))
                .put("theme", ticket.getTheme())
                .put("message", ticket.getMessage())
                .put("employeeName", ticket.getEmployee().getUsername())
                .put("customerName", ticket.getEmployee().getCustomer().getName())
                .put("page", ticket.getPageLocation().getDescription())
                .build();
        EmailNotification notification =
                new EmailNotification(email, "support_ticket_created", model);

        emailNotificationRepository.save(notification);
    }
}
