package dk.ledocsystem.service.impl;

import com.google.common.collect.ImmutableMap;
import dk.ledocsystem.data.model.email_notifications.EmailNotification;
import dk.ledocsystem.data.model.support_tickets.SupportTicket;
import dk.ledocsystem.service.api.dto.inbound.SupportTicketDTO;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import dk.ledocsystem.data.repository.EmailNotificationRepository;
import dk.ledocsystem.data.repository.EmployeeRepository;
import dk.ledocsystem.data.repository.SupportTicketRepository;
import dk.ledocsystem.service.api.SupportTicketService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.EMPLOYEE_ID_NOT_FOUND;

@Service
@AllArgsConstructor
class SupportTicketServiceImpl implements SupportTicketService {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");

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
        ticket.setCreated(LocalDateTime.now());
        ticket.setEmployee(employeeRepository.findById(supportTicketDTO.getEmployeeId())
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_ID_NOT_FOUND, supportTicketDTO.getEmployeeId().toString())));
        ticket.setTheme(supportTicketDTO.getTheme());
        ticket.setMessage(supportTicketDTO.getMessage());
        ticket.setPageLocation(supportTicketDTO.getPageLocation());
        ticket = supportTicketRepository.save(ticket);

        sendNotificationToResponsible(ticket,"testmytest43@gmail.com");
    }

    private void sendNotificationToResponsible(SupportTicket ticket, String email) {
        Map<String, Object> model = ImmutableMap.<String, Object>builder()
                .put("created", ticket.getCreated().format(dateTimeFormatter))
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
