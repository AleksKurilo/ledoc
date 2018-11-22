package dk.ledocsystem.service.api.dto.inbound.customer;

import dk.ledocsystem.service.api.validation.OnlyAscii;
import dk.ledocsystem.service.api.validation.PhoneNumber;
import dk.ledocsystem.service.api.validation.customer.UniqueCVR;
import dk.ledocsystem.service.api.validation.customer.UniqueName;
import dk.ledocsystem.service.api.dto.inbound.location.AddressDTO;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class CustomerCreateDTO {

    @NotNull
    @UniqueName
    @OnlyAscii
    @Size(min = 3, max = 40)
    private String name;

    @NotNull
    @UniqueCVR
    @OnlyAscii
    @Size(min = 3, max = 40)
    private String cvr;

    private Long pointOfContactId;

    @NotEmpty
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

    @Valid
    @NotNull
    private AddressDTO address;

    @Valid
    @NotNull
    private CustomerAdminDTO admin;
}
