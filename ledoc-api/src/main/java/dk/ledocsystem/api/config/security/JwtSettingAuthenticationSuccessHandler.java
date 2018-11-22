package dk.ledocsystem.api.config.security;

import com.google.common.collect.Collections2;
import dk.ledocsystem.service.api.EmployeeService;
import dk.ledocsystem.service.api.JwtTokenService;
import dk.ledocsystem.service.api.dto.outbound.employee.GetEmployeeDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static dk.ledocsystem.service.impl.constant.SecurityConstants.*;

/**
 * Handler that sets {@link HttpServletResponse} Authorization header to JWT user token,
 * containing username and authorities.
 */
@Service
@RequiredArgsConstructor
class JwtSettingAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenService tokenService;
    private final EmployeeService employeeService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        String token = getToken(authentication);
        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }

    private String getToken(Authentication authentication) {
        Instant expirationTime = LocalDateTime.now().plus(JWT_TOKEN_EXPIRATION_TIME).atZone(ZoneId.systemDefault()).toInstant();
        String username = authentication.getName();
        GetEmployeeDTO employee = employeeService.getByUsername(username).orElseThrow(IllegalStateException::new);

        String token = Jwts.builder()
                .setSubject(username)
                .claim(ID_CLAIM, employee.getId())
                .claim(JWT_AUTHORITIES_CLAIM, Collections2.transform(authentication.getAuthorities(), Object::toString))
                .setExpiration(Date.from(expirationTime))
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET.getBytes())
                .compact();
        tokenService.saveToken(token, employee.getId(), LocalDateTime.ofInstant(expirationTime, ZoneId.systemDefault()));
        return token;
    }
}
