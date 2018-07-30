package dk.ledocsystem.ledoc.config.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.time.*;

import static dk.ledocsystem.ledoc.config.security.SecurityConstants.*;

/**
 * Handler that sets {@link HttpServletResponse} Authorization header to JWT user token,
 * containing username and authorities.
 */
class JwtSettingAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        Instant expirationTime = LocalDateTime.now().plus(JWT_TOKEN_EXPIRATION_TIME).atZone(ZoneId.systemDefault()).toInstant();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim(JWT_AUTHORITIES_CLAIM, StringUtils.join(userDetails.getAuthorities(), ','))
                .setExpiration(Date.from(expirationTime))
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET.getBytes())
                .compact();
        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }
}
