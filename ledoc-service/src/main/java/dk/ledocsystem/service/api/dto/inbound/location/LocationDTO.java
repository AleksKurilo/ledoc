package dk.ledocsystem.service.api.dto.inbound.location;

import dk.ledocsystem.service.api.validation.NonCyrillic;
import dk.ledocsystem.data.model.LocationType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;
import java.util.Collections;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationDTO {

    private Long id;

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
    @NotNull(groups = AddressLocationValidation.class)
    private AddressDTO address;

    @NotNull(groups = PhysicalLocationValidation.class)
    private Long addressLocationId;

    private interface AddressLocationValidation {
        // validation group marker interface
    }

    private interface PhysicalLocationValidation {
        // validation group marker interface
    }

    public Class<?>[] getValidationGroups() {
        return (LocationType.ADDRESS.equals(type))
                ? new Class[]{AddressLocationValidation.class, Default.class}
                : new Class[]{PhysicalLocationValidation.class, Default.class};
    }
}
