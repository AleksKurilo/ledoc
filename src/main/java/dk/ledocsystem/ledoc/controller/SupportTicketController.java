package dk.ledocsystem.ledoc.controller;

import dk.ledocsystem.ledoc.dto.SupportTicketDTO;
import dk.ledocsystem.ledoc.service.SupportTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/support-ticket")
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    @PostMapping
    public void createTicket(@RequestBody SupportTicketDTO supportTicketDTO) {
        supportTicketService.createSupportTicket(supportTicketDTO);
    }

}
