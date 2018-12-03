package dk.ledocsystem.service.api.dto.inbound.location;

import dk.ledocsystem.service.api.validation.NonCyrillic;
import dk.ledocsystem.service.api.validation.location.LocationCreator;
import dk.ledocsystem.data.model.LocationType;
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
    @NonCyrillic
    private String name;

    private Long responsibleId;

    @NotNull
    @Builder.Default
    private Set<Long> employeeIds = Collections.emptySet();

    @Valid
    private AddressDTO address;

    private Long addressLocationId;
}
