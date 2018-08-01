package dk.ledocsystem.ledoc.service.impl;

import dk.ledocsystem.ledoc.dto.ChangePasswordDTO;
import dk.ledocsystem.ledoc.exceptions.InvalidCredentialsException;
import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.repository.EmployeeRepository;
import dk.ledocsystem.ledoc.service.ChangePasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
class ChangePasswordServiceImpl implements ChangePasswordService {

    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        Authentication auth = getCurrentUser().orElseThrow(IllegalStateException::new);
        String username = auth.getName();
        Employee employee = employeeRepository.findByUsername(username).orElseThrow(IllegalStateException::new);

        if (!passwordEncoder.matches(changePasswordDTO.getOldPassword(), employee.getPassword())) {
            throw new InvalidCredentialsException("Old password is incorrect");
        }

        String encodedNewPassword = passwordEncoder.encode(changePasswordDTO.getNewPassword());
        employee.setPassword(encodedNewPassword);
        employeeRepository.save(employee);
    }

    private Optional<Authentication> getCurrentUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }
}
