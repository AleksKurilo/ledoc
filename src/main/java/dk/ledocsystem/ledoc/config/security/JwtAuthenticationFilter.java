package dk.ledocsystem.ledoc.config.security;

import com.google.common.collect.Collections2;
import dk.ledocsystem.ledoc.exceptions.TokenNotFoundException;
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
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.Collection;

import static dk.ledocsystem.ledoc.config.security.SecurityConstants.*;

class JwtAuthenticationFilter extends BasicAuthenticationFilter {

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
            catch (TokenNotFoundException e) {
                Logger.error(e.getMessage());
                res.sendError(HttpStatus.FORBIDDEN.value(), "Invalid token. Login and get valid value.");
            }
            catch (IOException e) {
                Logger.error(e);
                res.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Unexpected error on server side.");
            }
        } else {
            res.sendError(HttpStatus.FORBIDDEN.value(), "Invalid or absent token.");
        }

        chain.doFilter(req, res);
    }

    private void validateToken(String inputToken, HttpServletResponse response) throws TokenNotFoundException, IOException {
        String validToken = tokenService.checkAndUpdateToken(inputToken);
        if (StringUtils.isBlank(validToken)) {
            throw new TokenNotFoundException("Invalid token. Token value is not in DB");
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

        Long userId = claims.get(ID_CLAIM, Long.class);
        String username = claims.getSubject();
        Long customerId = claims.get(CUSTOMER_CLAIM, Long.class);
        Principal principal = new UserPrincipal(userId, username, customerId);

        @SuppressWarnings("unchecked")
        Collection<String> authorities = (Collection<String>) claims.get(JWT_AUTHORITIES_CLAIM);
        return new UsernamePasswordAuthenticationToken(principal, null,
                Collections2.transform(authorities, SimpleGrantedAuthority::new));
    }
}
