package dk.ledocsystem.service.impl.constant;

import java.time.Period;
import java.time.temporal.TemporalAmount;

public final class SecurityConstants {
    public static final String JWT_SECRET = "A65991A920F0E88AFE321CE64393FE28C3F12D53FCDBC573E8AB7FED2FAC54B9";
    public static final String JWT_AUTHORITIES_CLAIM = "authorities";
    public static final String ID_CLAIM = "id";
    public static final TemporalAmount JWT_TOKEN_EXPIRATION_TIME = Period.ofDays(10);
}
