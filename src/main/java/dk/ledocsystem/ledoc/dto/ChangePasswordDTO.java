package dk.ledocsystem.ledoc.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import dk.ledocsystem.ledoc.annotations.validation.OnlyAscii;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ChangePasswordDTO {

    @NotNull(message = "Old password must not be null")
    @JsonAlias("old_password")
    private String oldPassword;

    @NotNull(message = "New password must not be null")
    @Size(min = 3, max = 40, message = "New password must be at least {min} and at most {max} characters long")
    @OnlyAscii(message = "New password must contain only ASCII characters")
    @JsonAlias("new_password")
    private String newPassword;
}
