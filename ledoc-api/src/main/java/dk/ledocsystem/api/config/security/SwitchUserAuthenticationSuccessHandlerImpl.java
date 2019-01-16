package dk.ledocsystem.api.config.security;

import com.google.common.collect.Collections2;
import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.repository.EmployeeRepository;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.switchuser.SwitchUserGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.EMPLOYEE_USERNAME_NOT_FOUND;
import static dk.ledocsystem.service.impl.constant.SecurityConstants.JWT_SECRET;

@Service
public class SwitchUserAuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {

    @Autowired
    private EmployeeRepository employeeRepository;

    private static final String ROLE_PREFIX = "ROLE_";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        Employee user = employeeRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(EMPLOYEE_USERNAME_NOT_FOUND, username));

        response.setHeader("Access-Control-Allow-Credentials", "true");

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("sub", username);
        claims.put("authorities", convertAuthorities(authentication.getAuthorities()
                .stream()
                .filter(a -> a instanceof SimpleGrantedAuthority)
                .map(a -> (SimpleGrantedAuthority) a)
                .collect(Collectors.toList())));

        List<? extends SwitchUserGrantedAuthority> ga = authentication.getAuthorities().stream().filter(a -> a instanceof SwitchUserGrantedAuthority).map(a -> (SwitchUserGrantedAuthority) a).collect(Collectors.toList());
        if (ga.size() > 0) {
            String responsible = ga.stream().findFirst().get().getSource().getName();
            claims.put("responsible", responsible);
        }

        JwtBuilder builder = Jwts.builder()
                .setAudience("user")
                .addClaims(claims)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET.getBytes());

        Cookie cookie = new Cookie("info", builder.compact());
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        response.addCookie(cookie);
    }

    private String[] convertAuthorities(List<SimpleGrantedAuthority> userAuthorities) {
        return Collections2.transform(userAuthorities, (auth) -> ROLE_PREFIX + auth.toString().substring(5).toLowerCase()).stream().toArray(String[]::new);
    }
}