package dk.ledocsystem.api.config.security;

import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.repository.EmployeeRepository;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.EMPLOYEE_USERNAME_NOT_FOUND;
import static dk.ledocsystem.service.impl.constant.SecurityConstants.JWT_SECRET;

@Service
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        Employee user = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_USERNAME_NOT_FOUND, username));

        response.setHeader("Access-Control-Allow-Credentials", "true");

        String domain = request.getServerName();

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("username", username);
        claims.put("authorities", authentication.getAuthorities().toArray());

        JwtBuilder builder = Jwts.builder()
                .setAudience("user")
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET.getBytes());

        Cookie cookie = new Cookie("info", builder.compact());
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}