package dk.ledocsystem.data.projections;

import dk.ledocsystem.data.model.LocationType;

public interface LocationSummary {

    Long getId();

    String getName();

    LocationType getType();
}
