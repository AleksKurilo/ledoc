package dk.ledocsystem.ledoc.dto;

import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ResetPasswordDTO {

    @NotNull
    private String token;

    @NotNull
    @Size(min = 3, max = 40)
    @OnlyAscii
    private String password;
}
