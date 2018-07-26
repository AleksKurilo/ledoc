package dk.ledocsystem.ledoc.config.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.time.*;

import static dk.ledocsystem.ledoc.config.security.JwtConstants.*;

@Component
class JwtSettingAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        Instant expirationTime = LocalDateTime.now().plus(TOKEN_EXPIRATION_TIME).atZone(ZoneId.systemDefault()).toInstant();
        String token = Jwts.builder()
                .setSubject(authentication.getPrincipal().toString())
                .setExpiration(Date.from(expirationTime))
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET.getBytes())
                .compact();
        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }
}
