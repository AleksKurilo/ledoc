package dk.ledocsystem.api.controller;

import dk.ledocsystem.api.config.security.CurrentUser;
import dk.ledocsystem.service.api.EmployeeService;
import dk.ledocsystem.service.api.JwtTokenService;
import dk.ledocsystem.service.api.dto.outbound.employee.GetEmployeeDTO;
import dk.ledocsystem.service.api.dto.outbound.employee.UserDetailsDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class LoginLogoutController {

    private final JwtTokenService tokenService;
    private final EmployeeService employeeService;

    @GetMapping("/userdetails")
    public UserDetailsDTO logout(@CurrentUser UserDetails currentUserDetails) {
        GetEmployeeDTO currentUser = employeeService.getByUsername(currentUserDetails.getUsername())
                .orElseThrow(IllegalStateException::new);
       return new UserDetailsDTO(currentUser.getId(), currentUserDetails.getUsername(), currentUserDetails.getAuthorities());
    }

    @PreAuthorize("isFullyAuthenticated()")
    @GetMapping("/logout")
    public void logout(HttpServletResponse response) {
        String token = response.getHeader(HttpHeaders.AUTHORIZATION).replace("Bearer ", "");
        tokenService.invalidateToken(token);
    }

    @PreAuthorize("isFullyAuthenticated()")
    @GetMapping("/offline")
    public void setOffline(HttpServletResponse response) {
        String token = response.getHeader(HttpHeaders.AUTHORIZATION).replace("Bearer ", "");
        tokenService.setUserOffline(token);
    }
}
