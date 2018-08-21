package dk.ledocsystem.ledoc.dto.location;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import dk.ledocsystem.ledoc.annotations.validation.location.LocationCreator;
import dk.ledocsystem.ledoc.annotations.validation.location.UniqueName;
import dk.ledocsystem.ledoc.model.LocationType;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@LocationCreator
public class LocationEditDTO {

    private LocationType type;

    @Size(min = 3, max = 40)
    @OnlyAscii
    @UniqueName
    private String name;

    private Long responsibleId;

    private Set<Long> employeeIds;

    @Valid
    @JsonDeserialize(using = AddressDeserializer.class)
    @JsonTypeInfo(use = JsonTypeInfo.Id.NONE)
    private AddressDTO address;

    private Long addressLocationId;
}
