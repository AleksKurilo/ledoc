package dk.ledocsystem.service.api.dto.inbound.employee;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeFollowDTO {

    private Long followerId;
    private boolean followed;
}
