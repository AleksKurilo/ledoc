package dk.ledocsystem.ledoc.service.impl;

import dk.ledocsystem.ledoc.dto.ForgotPasswordDTO;
import dk.ledocsystem.ledoc.dto.ResetPasswordDTO;
import dk.ledocsystem.ledoc.exceptions.InvalidCredentialsException;
import dk.ledocsystem.ledoc.exceptions.NotFoundException;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.model.ResetToken;
import dk.ledocsystem.ledoc.repository.EmployeeRepository;
import dk.ledocsystem.ledoc.repository.ResetTokenRepository;
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

    private final EmployeeRepository employeeRepository;
    private final ResetTokenRepository resetTokenRepository;
    private final SimpleMailService simpleMailService;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordDTO forgotPasswordDTO) {
        String email = forgotPasswordDTO.getEmail();
        Employee employee = employeeRepository.findByUsername(email)
                .orElseThrow(() -> new InvalidCredentialsException("Email " + email + " is invalid"));
        String token = UUID.randomUUID().toString();

        ResetToken resetToken = new ResetToken();
        resetToken.setEmployee(employee);
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
        Employee employee = resetToken.getEmployee();

        employee.setPassword(passwordEncoder.encode(resetPasswordDTO.getPassword()));

        resetTokenRepository.delete(resetToken);
        employeeRepository.save(employee);
    }

    private void sendResetEmail(String email, String resetUrl, String token) {
        String body = "To reset your password, click the link below:\n" +
                resetUrl + "/reset?token=" + token;
        simpleMailService.sendEmail(email, "Reset your password", body);
    }
}
