package dk.ledocsystem.ledoc.config.security;

import dk.ledocsystem.ledoc.exceptions.InvalidRoleNameException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum UserAuthorities {
    USER(0),
    ADMIN(1),
    SUPER_ADMIN(2),

    CAN_CREATE_PERSONAL_LOCATION(3);

    @Getter
    private final int code;

    public static UserAuthorities fromCode(int code) {
        for (UserAuthorities authorities : values()) {
            if (authorities.code == code) {
                return authorities;
            }
        }
        throw new IllegalArgumentException(Integer.toString(code) + " is illegal value for user authorities");
    }

    public static UserAuthorities fromString(String roleString) {
        String correctedRoleString = roleString.replaceAll("[-\\s]", "_");
        for (UserAuthorities authorities : values()) {
            if (authorities.toString().equalsIgnoreCase(correctedRoleString)) {
                return authorities;
            }
        }
        throw new InvalidRoleNameException(roleString);
    }
}
