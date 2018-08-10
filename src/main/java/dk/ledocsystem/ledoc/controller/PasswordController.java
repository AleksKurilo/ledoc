package dk.ledocsystem.ledoc.controller;

import dk.ledocsystem.ledoc.dto.ForgotPasswordDTO;
import dk.ledocsystem.ledoc.dto.ResetPasswordDTO;
import dk.ledocsystem.ledoc.service.ForgotPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/password")
public class PasswordController {

    private final ForgotPasswordService forgotPasswordService;

    @PostMapping("/forgot")
    public void forgotPassword(@RequestBody @Valid ForgotPasswordDTO forgotPasswordDTO) {
        forgotPasswordService.forgotPassword(forgotPasswordDTO);
    }

    @PostMapping("/reset")
    public void resetPassword(@RequestBody @Valid ResetPasswordDTO resetPasswordDTO) {
        forgotPasswordService.resetPassword(resetPasswordDTO);
    }

}
