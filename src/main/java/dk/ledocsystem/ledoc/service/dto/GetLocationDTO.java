package dk.ledocsystem.ledoc.service.dto;

import dk.ledocsystem.ledoc.model.LocationType;
import lombok.Data;

import java.util.Set;

@Data
public class GetLocationDTO {

    private String name;

    private LocationType type;

    private Long responsibleId;

    private GetAddressDTO address = new GetAddressDTO();

    private Long addressLocationId;

    private Set<Long> employeeIds;
}
