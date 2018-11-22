package dk.ledocsystem.service.api.dto.outbound.location;

import dk.ledocsystem.data.model.LocationType;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class LocationPreviewDTO {

    private String name;

    private LocationType type;

    private String responsibleName;

    private LocalDate creationDate;

    private String createdBy;

    private Long addressLocationId;

    private String addressLocationName;

    private List<String> employees;

    private GetAddressDTO address = new GetAddressDTO();
}
