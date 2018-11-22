package dk.ledocsystem.service.api.dto.inbound;

import dk.ledocsystem.service.api.validation.OnlyAscii;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ChangePasswordDTO {

    @NotNull
    @Size(min = 3, max = 40)
    @OnlyAscii
    private String newPassword;

}
