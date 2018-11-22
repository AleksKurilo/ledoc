package dk.ledocsystem.service.api.dto.outbound;

import lombok.Data;

import java.time.LocalDate;

@Data
public class GetDocumentDTO {

    private Long id;

    private String name;

    private LocalDate createOn;
}
