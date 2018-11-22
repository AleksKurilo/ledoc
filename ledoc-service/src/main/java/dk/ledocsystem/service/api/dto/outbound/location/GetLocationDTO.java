package dk.ledocsystem.service.api.dto.outbound.location;

import dk.ledocsystem.data.model.LocationType;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class GetLocationDTO {

    private Long id;

    private String name;

    private LocationType type;

    private LocalDate creationDate;

    private String responsible;

    private GetAddressDTO address = new GetAddressDTO();

    private Long addressLocationId;

    private List<PhysicalLocationDTO> physicalLocations;
}
