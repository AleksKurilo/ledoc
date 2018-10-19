package dk.ledocsystem.ledoc.controller;

import dk.ledocsystem.ledoc.dto.ForgotPasswordDTO;
import dk.ledocsystem.ledoc.dto.ResetPasswordDTO;
import dk.ledocsystem.ledoc.service.ForgotPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/password")
public class PasswordController {

    private final ForgotPasswordService forgotPasswordService;

    @PostMapping("/forgot")
    public void forgotPassword(@RequestBody ForgotPasswordDTO forgotPasswordDTO) {
        forgotPasswordService.forgotPassword(forgotPasswordDTO);
    }

    @PostMapping("/reset")
    public void resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {
        forgotPasswordService.resetPassword(resetPasswordDTO);
    }

}
