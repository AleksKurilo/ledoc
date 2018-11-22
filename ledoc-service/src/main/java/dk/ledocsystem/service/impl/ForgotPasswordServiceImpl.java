package dk.ledocsystem.service.impl;

import com.google.common.collect.ImmutableMap;
import dk.ledocsystem.service.api.dto.inbound.ForgotPasswordDTO;
import dk.ledocsystem.service.api.dto.inbound.ResetPasswordDTO;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import dk.ledocsystem.data.model.email_notifications.EmailNotification;
import dk.ledocsystem.data.model.security.ResetToken;
import dk.ledocsystem.data.repository.EmailNotificationRepository;
import dk.ledocsystem.data.repository.ResetTokenRepository;
import dk.ledocsystem.service.api.EmployeeService;
import dk.ledocsystem.service.api.ForgotPasswordService;
import dk.ledocsystem.service.impl.validators.BaseValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.RESET_TOKEN_NOT_FOUND;

@Service
@RequiredArgsConstructor
class ForgotPasswordServiceImpl implements ForgotPasswordService {
    private final EmployeeService employeeService;
    private final ResetTokenRepository resetTokenRepository;
    private final EmailNotificationRepository emailNotificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final BaseValidator<ForgotPasswordDTO> forgotPasswordDtoValidator;
    private final BaseValidator<ResetPasswordDTO> resetPasswordDtoValidator;

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordDTO forgotPasswordDTO) {
        forgotPasswordDtoValidator.validate(forgotPasswordDTO);

        String email = forgotPasswordDTO.getEmail();
        String token = UUID.randomUUID().toString();
        ResetToken resetToken = new ResetToken(email, token);
        resetTokenRepository.save(resetToken);

        sendResetEmail(email, forgotPasswordDTO.getResetUrl(), token);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
        resetPasswordDtoValidator.validate(resetPasswordDTO);

        String token = resetPasswordDTO.getToken();
        ResetToken resetToken = resetTokenRepository.findByToken(token)
                .orElseThrow(() -> new NotFoundException(RESET_TOKEN_NOT_FOUND, token));

        String encodedPassword = passwordEncoder.encode(resetPasswordDTO.getPassword());

        resetTokenRepository.delete(resetToken);
        employeeService.changePassword(resetToken.getUsername(), encodedPassword);
    }

    private void sendResetEmail(String destination, String resetUrl, String token) {
        String link = resetUrl + "?" + token;
        Map<String, Object> model = ImmutableMap.of("link", link);
        EmailNotification linkEmail = new EmailNotification(destination, "reset_password", model);

        emailNotificationRepository.save(linkEmail);
    }
}
