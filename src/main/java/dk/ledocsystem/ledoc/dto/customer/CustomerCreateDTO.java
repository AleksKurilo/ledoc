package dk.ledocsystem.ledoc.dto.customer;

import com.fasterxml.jackson.annotation.JsonAlias;
import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import dk.ledocsystem.ledoc.annotations.validation.customer.MobilePhone;
import dk.ledocsystem.ledoc.annotations.validation.customer.UniqueCVR;
import dk.ledocsystem.ledoc.annotations.validation.customer.UniqueName;
import dk.ledocsystem.ledoc.dto.location.AddressCreateDTO;
import dk.ledocsystem.ledoc.dto.employee.EmployeeCreateDTO;
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

    // id of the superadmin
    private Long pointOfContactId;

    @NotEmpty
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
    @NotNull
    private AddressCreateDTO address;

    @Valid
    @NotNull
    @JsonAlias({"employee", "admin"})
    private EmployeeCreateDTO employeeCreateDTO;
}
