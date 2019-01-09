package dk.ledocsystem.api.controller;

import dk.ledocsystem.service.api.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class LoginLogoutController {

    private final JwtTokenService tokenService;
    @Autowired
    private SessionRegistry sessionRegistry;


    @GetMapping("/concurrent")
    public int concurrent() {
        return sessionRegistry.getAllPrincipals().size();
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
