package dk.ledocsystem.ledoc.service.impl;

import com.google.common.collect.ImmutableMap;
import dk.ledocsystem.ledoc.dto.ForgotPasswordDTO;
import dk.ledocsystem.ledoc.dto.ResetPasswordDTO;
import dk.ledocsystem.ledoc.exceptions.InvalidCredentialsException;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.security.ResetToken;
import dk.ledocsystem.ledoc.repository.ResetTokenRepository;
import dk.ledocsystem.ledoc.service.EmailTemplateService;
import dk.ledocsystem.ledoc.service.EmployeeService;
import dk.ledocsystem.ledoc.service.ForgotPasswordService;
import dk.ledocsystem.ledoc.service.SimpleMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class ForgotPasswordServiceImpl implements ForgotPasswordService {
    private final EmployeeService employeeService;
    private final ResetTokenRepository resetTokenRepository;
    private final SimpleMailService simpleMailService;
    private final PasswordEncoder passwordEncoder;
    private final EmailTemplateService emailTemplateService;

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordDTO forgotPasswordDTO) {
        String email = forgotPasswordDTO.getEmail();
        if (!employeeService.existsByUsername(email)) {
            throw new InvalidCredentialsException("username.not.found");
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
                .orElseThrow(() -> new NotFoundException("reset.token.not.found", token));

        String encodedPassword = passwordEncoder.encode(resetPasswordDTO.getPassword());

        resetTokenRepository.delete(resetToken);
        employeeService.changePassword(resetToken.getUsername(), encodedPassword);
    }

    private void sendResetEmail(String destination, String resetUrl, String token) {
        String link = resetUrl + "?" + token;

        EmailTemplateService.EmailTemplate template = emailTemplateService.getTemplateLocalized("reset_password");
        Object model = ImmutableMap.of("link", link);
        String html = template.parseTemplate(model);

        simpleMailService.sendMimeMessage(destination, template.getSubject(), html);
    }
}
