package dk.ledocsystem.service.api.dto.inbound.customer;

import dk.ledocsystem.service.api.validation.OnlyAscii;
import dk.ledocsystem.service.api.validation.PhoneNumber;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class CustomerEditDTO {

    @NotNull
    private Long id;

    @OnlyAscii
    @Size(min = 3, max = 40)
    private String name;

    @OnlyAscii
    @Size(min = 3, max = 40)
    private String cvr;

    // id of the superadmin
    private Long pointOfContactId;

    private Set<Long> tradeIds;

    @PhoneNumber
    private String contactPhone;

    @Email
    @Size(min = 8, max = 40)
    private String contactEmail;

    @Email
    @Size(min = 8, max = 40)
    private String invoiceEmail;

    @Email
    @Size(min = 8, max = 40)
    private String companyEmail;

    @Email
    @Size(min = 8, max = 40)
    private String mailbox;
}
