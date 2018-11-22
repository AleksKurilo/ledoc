package dk.ledocsystem.service.api.dto.outbound.location;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PhysicalLocationDTO {

    private Long id;

    private String name;

    private LocalDate creationDate;

    private Long addressLocationId;
}
