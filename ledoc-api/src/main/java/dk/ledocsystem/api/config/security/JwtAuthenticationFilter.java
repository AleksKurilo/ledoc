package dk.ledocsystem.api.config.security;

import com.google.common.collect.Collections2;
import dk.ledocsystem.service.api.JwtTokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.StringUtils;
import org.pmw.tinylog.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

import static dk.ledocsystem.service.impl.constant.SecurityConstants.*;

class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    private static final String NO_PASSWORD = "";

    private JwtTokenService tokenService;

    JwtAuthenticationFilter(AuthenticationManager authManager, JwtTokenService jwtTokenService) {
        super(authManager);
        tokenService = jwtTokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith("Bearer ")) {
            String tokenValue = header.replace("Bearer ", "");
            try {
                validateToken(tokenValue, res);
            }

            catch (IOException e) {
                Logger.error(e);
                res.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error on server side.");
            }
            catch (Exception e) {
                Logger.error(e.getMessage());
                res.sendError(HttpStatus.FORBIDDEN.value(), e.getMessage());
            }
        }

        chain.doFilter(req, res);
    }

    private void validateToken(String inputToken, HttpServletResponse response) throws Exception {
        String validToken = tokenService.checkAndUpdateToken(inputToken);
        if (StringUtils.isBlank(validToken)) {
            throw new Exception("Invalid token. Token value is not in DB");
        } else {
            if (!inputToken.equals(validToken)) {
                response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + validToken);
            }
            prepareAuthorization(validToken, response);
        }
    }

    private void prepareAuthorization(String token, HttpServletResponse response) throws IOException {
        try {
            Authentication authentication = getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        catch (ExpiredJwtException ex) {
            tokenService.invalidateToken(token);
            response.sendError(HttpStatus.FORBIDDEN.value(), "Token has expired");
        }
    }

    private Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(JWT_SECRET.getBytes())
            .parseClaimsJws(token)
            .getBody();

        String username = claims.getSubject();
        @SuppressWarnings("unchecked")
        Collection<String> authorityStrings = (Collection<String>) claims.get(JWT_AUTHORITIES_CLAIM);
        Collection<SimpleGrantedAuthority> authorities = Collections2.transform(authorityStrings, SimpleGrantedAuthority::new);

        UserDetails user = new User(username, NO_PASSWORD, authorities);
        return new UsernamePasswordAuthenticationToken(user, null, authorities);
    }
}
