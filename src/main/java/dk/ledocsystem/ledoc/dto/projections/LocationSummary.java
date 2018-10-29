package dk.ledocsystem.ledoc.dto.projections;

import dk.ledocsystem.ledoc.model.LocationType;

public interface LocationSummary {

    Long getId();

    String getName();

    LocationType getType();
}
