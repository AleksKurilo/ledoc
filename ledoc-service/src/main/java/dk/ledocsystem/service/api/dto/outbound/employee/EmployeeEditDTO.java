package dk.ledocsystem.service.api.dto.outbound.employee;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class EmployeeEditDTO extends GetEmployeeDTO {

    private Set<Long> locationIds;

    private Long responsibleId;
}
