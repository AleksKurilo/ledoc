package dk.ledocsystem.ledoc.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.ledocsystem.ledoc.exceptions.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        Locale locale = localeResolver.resolveLocale(request);
        String message;

        if (exception instanceof UsernameNotFoundException) {
            message = messageSource.getMessage("username.not.found", null, locale);
        } else {
            message = messageSource.getMessage("username.password.invalid", null, locale);
        }

        RestResponse restResponse = new RestResponse(message);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), restResponse);
    }
}
