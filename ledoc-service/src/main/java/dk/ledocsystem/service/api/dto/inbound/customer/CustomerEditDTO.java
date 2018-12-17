package dk.ledocsystem.service.api.dto.inbound.customer;

import dk.ledocsystem.service.api.validation.NonCyrillic;
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

    @NotNull
    @NonCyrillic
    @Size(min = 3, max = 40)
    private String name;

    @NotNull
    @NonCyrillic
    @Size(min = 3, max = 40)
    private String cvr;

    @NotNull
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
