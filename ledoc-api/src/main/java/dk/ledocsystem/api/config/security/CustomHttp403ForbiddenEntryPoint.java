package dk.ledocsystem.api.config.security;

import lombok.NoArgsConstructor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@NoArgsConstructor
public class CustomHttp403ForbiddenEntryPoint extends Http403ForbiddenEntryPoint {
    private static final Log logger = LogFactory.getLog(CustomHttp403ForbiddenEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException arg2) throws IOException, ServletException {
        if (logger.isDebugEnabled()) {
            logger.debug("Pre-authenticated entry point called. Rejecting access");
        }

        if (request.getSession(false) == null) {
            Arrays.stream(request.getCookies()).forEach(cookie -> {
                Cookie cookieToDelete = new Cookie(cookie.getName(), null);
                cookieToDelete.setPath("/");
                cookieToDelete.setMaxAge(0);
                response.addCookie(cookieToDelete);
            });
        }
        response.sendError(403, "Access Denied");
    }
}

