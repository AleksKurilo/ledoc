package dk.ledocsystem.ledoc.service;

import dk.ledocsystem.ledoc.dto.ForgotPasswordDTO;
import dk.ledocsystem.ledoc.dto.ResetPasswordDTO;
import dk.ledocsystem.ledoc.model.security.ResetToken;

public interface ForgotPasswordService {

    /**
     * Sends an email with the token to reset password.
     *
     * @param forgotPasswordDTO Data required to request password reset
     */
    void forgotPassword(ForgotPasswordDTO forgotPasswordDTO);

    /**
     * Updates password using {@link ResetToken}.
     *
     * @param resetPasswordDTO Data required to reset password
     */
    void resetPassword(ResetPasswordDTO resetPasswordDTO);
}
