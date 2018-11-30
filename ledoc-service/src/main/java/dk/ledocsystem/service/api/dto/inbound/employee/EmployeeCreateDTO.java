package dk.ledocsystem.service.api.dto.inbound.employee;

import dk.ledocsystem.service.api.validation.OnlyAscii;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
public class EmployeeCreateDTO extends EmployeeDTO {

    @NotNull
    @Size(min = 5, max = 40)
    @OnlyAscii
    private String password;

    private boolean welcomeMessage;
}
