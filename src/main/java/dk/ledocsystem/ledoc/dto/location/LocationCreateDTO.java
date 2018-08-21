package dk.ledocsystem.ledoc.dto.location;

import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import dk.ledocsystem.ledoc.annotations.validation.location.LocationCreator;
import dk.ledocsystem.ledoc.annotations.validation.location.UniqueName;
import dk.ledocsystem.ledoc.model.LocationType;
import lombok.Builder;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.Set;

@Data
@Builder
@LocationCreator
public class LocationCreateDTO {

    @NotNull
    private LocationType type;

    @NotNull
    @Size(min = 3, max = 40)
    @OnlyAscii
    @UniqueName
    private String name;

    @NotNull
    private Long responsibleId;

    @Builder.Default
    private Set<Long> employeeIds = Collections.emptySet();

    @Valid
    private AddressCreateDTO address;

    private Long addressLocationId;
}
