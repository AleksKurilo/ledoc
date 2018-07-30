package dk.ledocsystem.ledoc.config.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.Period;
import java.time.temporal.TemporalAmount;

final class SecurityConstants {
    static final String JWT_SECRET = "A65991A920F0E88AFE321CE64393FE28C3F12D53FCDBC573E8AB7FED2FAC54B9";
    static final String JWT_AUTHORITIES_CLAIM = "authorities";
    static final TemporalAmount JWT_TOKEN_EXPIRATION_TIME = Period.ofDays(10);
    static final GrantedAuthority ADMIN_AUTHORITY = new SimpleGrantedAuthority("ROLE_admin");
}
