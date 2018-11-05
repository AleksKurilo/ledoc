package dk.ledocsystem.ledoc.dto.location;

import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import dk.ledocsystem.ledoc.annotations.validation.location.LocationCreator;
import dk.ledocsystem.ledoc.model.LocationType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@LocationCreator
public class LocationDTO {

    private Long id;
    private Long customerId; //todo should not be here

    @NotNull
    private LocationType type;

    @NotNull
    @Size(min = 3, max = 40)
    @OnlyAscii
    private String name;

    private Long responsibleId;

    @NotNull
    @Builder.Default
    private Set<Long> employeeIds = Collections.emptySet();

    @Valid
    private AddressDTO address;

    private Long addressLocationId;
}
