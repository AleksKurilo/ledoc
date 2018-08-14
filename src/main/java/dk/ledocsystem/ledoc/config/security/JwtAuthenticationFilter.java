package dk.ledocsystem.ledoc.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.List;

import static dk.ledocsystem.ledoc.config.security.SecurityConstants.CUSTOMER_CLAIM;
import static dk.ledocsystem.ledoc.config.security.SecurityConstants.JWT_AUTHORITIES_CLAIM;
import static dk.ledocsystem.ledoc.config.security.SecurityConstants.JWT_SECRET;

class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    JwtAuthenticationFilter(AuthenticationManager authManager) {
        super(authManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(HttpHeaders.AUTHORIZATION);

        if (header != null && header.startsWith("Bearer ")) {
            Authentication authentication = getAuthentication(req);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(req, res);
    }

    private Authentication getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null) {
            Claims claims = Jwts.parser()
                    .setSigningKey(JWT_SECRET.getBytes())
                    .parseClaimsJws(token.replace("Bearer ", ""))
                    .getBody();

            String username = claims.getSubject();
            Long customerId = claims.get(CUSTOMER_CLAIM, Long.class);
            Principal principal = new UserPrincipal(username, customerId);

            if (username != null) {
                List<GrantedAuthority> authorities =
                        AuthorityUtils.commaSeparatedStringToAuthorityList(claims.get(JWT_AUTHORITIES_CLAIM, String.class));
                return new UsernamePasswordAuthenticationToken(principal, null, authorities);
            }
            return null;
        }
        return null;
    }
}
