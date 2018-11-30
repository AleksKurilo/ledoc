package dk.ledocsystem.service.api.dto.inbound.employee;

import dk.ledocsystem.service.api.validation.Password;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class EmployeeCreateDTO extends EmployeeDTO {

    @NotNull
    @Password
    private String password;

    private boolean welcomeMessage;
}
