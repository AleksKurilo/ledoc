package dk.ledocsystem.service.api;

import dk.ledocsystem.service.api.dto.inbound.ForgotPasswordDTO;
import dk.ledocsystem.service.api.dto.inbound.ResetPasswordDTO;
import dk.ledocsystem.data.model.security.ResetToken;

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
