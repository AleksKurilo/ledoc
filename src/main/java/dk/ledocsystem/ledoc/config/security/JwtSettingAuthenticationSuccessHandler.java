package dk.ledocsystem.ledoc.config.security;

import dk.ledocsystem.ledoc.model.employee.Employee;
import dk.ledocsystem.ledoc.repository.EmployeeRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Date;
import java.time.*;

import static dk.ledocsystem.ledoc.config.security.SecurityConstants.*;

/**
 * Handler that sets {@link HttpServletResponse} Authorization header to JWT user token,
 * containing username and authorities.
 */
@Service
@RequiredArgsConstructor
class JwtSettingAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenRegistry tokenRegistry;
    private final EmployeeRepository employeeRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        Instant expirationTime = LocalDateTime.now().plus(JWT_TOKEN_EXPIRATION_TIME).atZone(ZoneId.systemDefault()).toInstant();
        String token = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(JWT_AUTHORITIES_CLAIM, StringUtils.join(authentication.getAuthorities(), ','))
                .claim(CUSTOMER_CLAIM, getCustomerId(authentication))
                .setExpiration(Date.from(expirationTime))
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET.getBytes())
                .compact();
        tokenRegistry.saveToken(token, System.nanoTime());
        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }

    private Long getCustomerId(Authentication authentication) {
        String username = authentication.getName();
        Employee employee = employeeRepository.findByUsername(username).orElseThrow(IllegalStateException::new);
        return employee.getCustomer().getId();
    }
}
