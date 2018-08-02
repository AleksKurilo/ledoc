package dk.ledocsystem.ledoc.service.impl;

import dk.ledocsystem.ledoc.dto.ForgotPasswordDTO;
import dk.ledocsystem.ledoc.dto.ResetPasswordDTO;
import dk.ledocsystem.ledoc.exceptions.InvalidCredentialsException;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.ResetToken;
import dk.ledocsystem.ledoc.repository.ResetTokenRepository;
import dk.ledocsystem.ledoc.service.EmployeeService;
import dk.ledocsystem.ledoc.service.ForgotPasswordService;
import dk.ledocsystem.ledoc.service.SimpleMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class ForgotPasswordServiceImpl implements ForgotPasswordService {
    private static final String SUBJECT = "Reset your password";

    private final EmployeeService employeeService;
    private final ResetTokenRepository resetTokenRepository;
    private final SimpleMailService simpleMailService;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.mail.username}")
    private String fromEmailAddress;

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordDTO forgotPasswordDTO) {
        String email = forgotPasswordDTO.getEmail();
        if (!employeeService.existsByUsername(email)) {
            throw new InvalidCredentialsException("Email " + email + " is invalid");
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
                .orElseThrow(() -> new NotFoundException(ResetToken.class, token));

        String encodedPassword = passwordEncoder.encode(resetPasswordDTO.getPassword());

        resetTokenRepository.delete(resetToken);
        employeeService.changePassword(resetToken.getUsername(), encodedPassword);
    }

    private void sendResetEmail(String destination, String resetUrl, String token) {
        String link = resetUrl + "?" + token;
        String body = "To reset your password, click the link below:\n<a href=\"" +
                link + "\">" + link + "</a>";
        simpleMailService.sendEmail(fromEmailAddress, destination, SUBJECT, body);
    }
}
