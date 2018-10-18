package dk.ledocsystem.ledoc.service.impl;

import com.google.common.collect.ImmutableMap;
import dk.ledocsystem.ledoc.dto.ForgotPasswordDTO;
import dk.ledocsystem.ledoc.dto.ResetPasswordDTO;
import dk.ledocsystem.ledoc.exceptions.InvalidCredentialsException;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.email_notifications.EmailNotification;
import dk.ledocsystem.ledoc.model.security.ResetToken;
import dk.ledocsystem.ledoc.repository.EmailNotificationRepository;
import dk.ledocsystem.ledoc.repository.ResetTokenRepository;
import dk.ledocsystem.ledoc.service.EmployeeService;
import dk.ledocsystem.ledoc.service.ForgotPasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static dk.ledocsystem.ledoc.constant.ErrorMessageKey.RESET_TOKEN_NOT_FOUND;
import static dk.ledocsystem.ledoc.constant.ErrorMessageKey.USER_NAME_NOT_FOUND;

@Service
@RequiredArgsConstructor
class ForgotPasswordServiceImpl implements ForgotPasswordService {
    private final EmployeeService employeeService;
    private final ResetTokenRepository resetTokenRepository;
    private final EmailNotificationRepository emailNotificationRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordDTO forgotPasswordDTO) {
        String email = forgotPasswordDTO.getEmail();
        if (!employeeService.existsByUsername(email)) {
            throw new InvalidCredentialsException(USER_NAME_NOT_FOUND);
        }

        String token = UUID.randomUUID().toString();
        ResetToken resetToken = new ResetToken();
        resetToken.setUsername(email);
        resetToken.setToken(token);
        resetTokenRepository.save(resetToken);

        sendResetEmail(forgotPasswordDTO.getEmail(), forgotPasswordDTO.getResetUrl(), token);
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordDTO resetPasswordDTO) {
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
