package dk.ledocsystem.ledoc.dto.employee;

import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@EqualsAndHashCode(callSuper = true)
public class EmployeeCreateDTO extends EmployeeDTO {

    @NotNull
    @Size(min = 3, max = 40)
    @OnlyAscii
    private String password;

    private boolean welcomeMessage;
}
