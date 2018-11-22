package dk.ledocsystem.service.api.dto.inbound;

import dk.ledocsystem.data.model.support_tickets.PageLocation;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Setter
@Getter
public class SupportTicketDTO {
    private Long employeeId;

    @NotNull
    @Size(min = 2, max = 40)
    private String theme;

    @NotNull
    @Size(min = 2, max = 4000)
    private String message;

    private PageLocation pageLocation;
}
