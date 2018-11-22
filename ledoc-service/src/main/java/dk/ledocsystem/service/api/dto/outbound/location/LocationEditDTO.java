package dk.ledocsystem.service.api.dto.outbound.location;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class LocationEditDTO extends GetLocationDTO {

    private Long responsibleId;

    private Set<Long> employeeIds;
}
