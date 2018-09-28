package dk.ledocsystem.ledoc.dto.customer;

import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import dk.ledocsystem.ledoc.annotations.validation.PhoneNumber;
import dk.ledocsystem.ledoc.annotations.validation.customer.UniqueCVR;
import dk.ledocsystem.ledoc.annotations.validation.customer.UniqueName;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class CustomerEditDTO {
    @UniqueName
    @OnlyAscii
    @Size(min = 3, max = 40)
    private String name;

    @UniqueCVR
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

    private Boolean archived;
}
