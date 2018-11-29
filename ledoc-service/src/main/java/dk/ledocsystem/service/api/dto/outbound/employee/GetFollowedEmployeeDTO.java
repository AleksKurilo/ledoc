package dk.ledocsystem.service.api.dto.outbound.employee;

import lombok.Data;

@Data
public class GetFollowedEmployeeDTO {

    private Long id;

    private String name;

    private boolean forced;
}
