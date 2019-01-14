package dk.ledocsystem.service.api.dto.outbound.supplier;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GetSupplierDTO {

    private Long id;

    private String name;

    private String description;

    private String contactPhone;

    private String contactEmail;

    private LocalDate nextReviewDate;

    private String category;

    private String responsible;

    private String reviewResponsible;
}
