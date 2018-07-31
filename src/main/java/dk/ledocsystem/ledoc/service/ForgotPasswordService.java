package dk.ledocsystem.ledoc.service;

import dk.ledocsystem.ledoc.dto.ResetPasswordDTO;

public interface ForgotPasswordService {

    /**
     * Sends an email with the token to reset password.
     *
     * @param email  Email identifying the user whose password to be reset
     * @param appUrl URL of the application
     */
    void forgotPassword(String email, String appUrl);

    /**
     * Updates password using {@link dk.ledocsystem.ledoc.model.ResetToken}.
     *
     * @param resetPasswordDTO Data required to reset password
     */
    void resetPassword(ResetPasswordDTO resetPasswordDTO);
}
