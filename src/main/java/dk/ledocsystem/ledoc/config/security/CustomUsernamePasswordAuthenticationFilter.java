package dk.ledocsystem.ledoc.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

class CustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    CustomUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager) {
        setAuthenticationManager(authenticationManager);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException(
                    "Authentication method not supported: " + request.getMethod());
        }

        UserCreds userCredentials = obtainUserCredentials(request);

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                userCredentials.getUsername(), userCredentials.getPassword());

        setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    private UserCreds obtainUserCredentials(HttpServletRequest request) {
        try {
            return objectMapper.reader()
                    .forType(UserCreds.class)
                    .readValue(request.getReader());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Data
    private static class UserCreds {
        String username, password;
    }

}
