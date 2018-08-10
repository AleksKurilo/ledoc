package dk.ledocsystem.ledoc.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ForgotPasswordDTO {

    @NotNull
    private String resetUrl;

    @NotNull
    private String email;
}
