package dk.ledocsystem.service.api.dto.outbound;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GetCustomerDTO {

    private Long id;

    private String name;

    private String cvr;

    private String contactPhone;

    private String contactEmail;

    private String invoiceEmail;

    private String companyEmail;

    private String mailbox;

    private LocalDate dateOfCreation;
}
