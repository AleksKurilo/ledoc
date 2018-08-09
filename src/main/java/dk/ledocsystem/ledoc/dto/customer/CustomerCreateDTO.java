package dk.ledocsystem.ledoc.dto.customer;

import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import dk.ledocsystem.ledoc.annotations.validation.customer.MobilePhone;
import dk.ledocsystem.ledoc.annotations.validation.customer.UniqueCVR;
import dk.ledocsystem.ledoc.annotations.validation.customer.UniqueName;
import dk.ledocsystem.ledoc.dto.AddressDTO;
import dk.ledocsystem.ledoc.dto.EmployeeDTO;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
public class CustomerCreateDTO {

    @NotNull(message = "Name must not be null")
    @UniqueName
    @OnlyAscii(message = "Name must contain only ASCII characters")
    @Size(min = 3, max = 40, message = "Company's name must be at least {min} and at most {max} characters long")
    private String name;

    @NotNull(message = "CVR must not be null")
    @UniqueCVR
    @OnlyAscii(message = "Company's name must contain only ASCII characters")
    @Size(min = 3, max = 40, message = "CVR must be at least {min} and at most {max} characters long")
    private String cvr;

    // id of the superadmin
    private Long pointOfContactId;

    private Set<Long> tradeIds;

    @MobilePhone
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
    private AddressDTO addressDTO;

    @Valid
    private EmployeeDTO employeeDTO;
}
