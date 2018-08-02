package dk.ledocsystem.ledoc.config.security;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum UserAuthorities {
    USER(0),
    ADMIN(1),
    SUPER_ADMIN(2);

    @Getter
    public final int code;

    static UserAuthorities fromCode(int code) {
        for (UserAuthorities authorities : values()) {
            if (authorities.code == code) {
                return authorities;
            }
        }
        throw new IllegalArgumentException(Integer.toString(code) + " is illegal value for user authorities");
    }
}
