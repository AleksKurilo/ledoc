package dk.ledocsystem.ledoc.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ForgotPasswordDTO {

    @NotNull(message = "Reset URL must not be null")
    @JsonAlias("reset_url")
    private String resetUrl;

    @NotNull(message = "Email must not be null")
    private String email;
}
