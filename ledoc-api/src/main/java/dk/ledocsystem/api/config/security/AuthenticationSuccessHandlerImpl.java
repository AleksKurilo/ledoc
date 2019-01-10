package dk.ledocsystem.api.config.security;

import dk.ledocsystem.data.model.employee.Employee;
import dk.ledocsystem.data.repository.EmployeeRepository;
import dk.ledocsystem.service.api.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.EMPLOYEE_USERNAME_NOT_FOUND;

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

        Cookie cookie = new Cookie("id", user.getId().toString());
        response.addCookie(cookie);

        cookie = new Cookie("email", username);
        response.addCookie(cookie);

        cookie = new Cookie("authorities", authentication.getAuthorities().toString());
        response.addCookie(cookie);
    }
}