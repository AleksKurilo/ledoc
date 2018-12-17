package dk.ledocsystem.service.api.dto.inbound.customer;

import dk.ledocsystem.service.api.validation.NonCyrillic;
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
import javax.validation.groups.Default;
import java.util.Set;

@Data
public class CustomerCreateDTO {

    @NotNull
    @UniqueName
    @NonCyrillic
    @Size(min = 3, max = 40)
    private String name;

    @NotNull
    @UniqueCVR
    @NonCyrillic
    @Size(min = 3, max = 40)
    private String cvr;

    @NotNull
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

    public Class<?>[] getValidationGroups() {
        return (admin == null) ? new Class[] {Default.class} : admin.getValidationGroups();
    }
}
