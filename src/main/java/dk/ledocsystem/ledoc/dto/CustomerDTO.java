package dk.ledocsystem.ledoc.dto;

import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import dk.ledocsystem.ledoc.annotations.validation.MobilePhone;
import dk.ledocsystem.ledoc.annotations.validation.customer.UniqueCVR;
import dk.ledocsystem.ledoc.annotations.validation.customer.UniqueName;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class CustomerDTO {

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

    @Size(min = 3, max = 20, message = "Contact phone must be at least {min} and at most {max} characters long")
    @MobilePhone
    private String contactPhone;

    @Email
    @Size(min = 15, max = 100, message = "Contact email must be at least {min} and at most {max} characters long")
    private String contactEmail;

    @Email
    @Size(min = 15, max = 100, message = "Invoice email must be at least {min} and at most {max} characters long")
    private String invoiceEmail;

    @Email
    @Size(min = 15, max = 100, message = "Company email must be at least {min} and at most {max} characters long")
    private String companyEmail;
}
