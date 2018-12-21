package dk.ledocsystem.service.api.dto.outbound.location;

import dk.ledocsystem.data.model.LocationType;
import lombok.Data;

@Data
public class LocationSummary {

    private Long id;

    private String name;

    private LocationType type;
}
