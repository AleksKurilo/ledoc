package dk.ledocsystem.service.api.dto.inbound;

import dk.ledocsystem.service.api.validation.Password;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ResetPasswordDTO {

    @NotNull
    private String token;

    @NotNull
    @Password
    private String password;
}
