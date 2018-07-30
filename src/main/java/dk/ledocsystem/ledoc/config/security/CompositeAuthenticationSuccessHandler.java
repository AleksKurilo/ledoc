package dk.ledocsystem.ledoc.config.security;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@AllArgsConstructor
class CompositeAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private List<AuthenticationSuccessHandler> handlers;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (handlers != null) {
            for (AuthenticationSuccessHandler handler : handlers) {
                handler.onAuthenticationSuccess(request, response, authentication);
            }
        }
    }
}
