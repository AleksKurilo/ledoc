package dk.ledocsystem.api.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.ledocsystem.api.exceptions.RestResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.*;

@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        Locale locale = localeResolver.resolveLocale(request);
        Map<String, List<String>> errorMap = new HashMap<>();

        if (exception instanceof UsernameNotFoundException) {
            String message = messageSource.getMessage(USER_NAME_NOT_FOUND, null, locale);
            errorMap.put("username", Collections.singletonList(message));
        } else {
            String message = messageSource.getMessage(USER_PASSWORD_INVALID, null, locale);
            errorMap.put("password", Collections.singletonList(message));
        }

        RestResponse restResponse = new RestResponse(errorMap);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        objectMapper.writeValue(response.getWriter(), restResponse);
    }
}
