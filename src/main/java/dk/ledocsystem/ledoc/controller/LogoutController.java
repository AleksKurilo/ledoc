package dk.ledocsystem.ledoc.controller;

import dk.ledocsystem.ledoc.config.security.JwtTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
public class LogoutController {

    private final JwtTokenService tokenService;

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
