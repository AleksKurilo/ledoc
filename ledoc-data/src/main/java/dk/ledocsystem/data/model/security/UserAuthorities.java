package dk.ledocsystem.data.model.security;

import dk.ledocsystem.data.exceptions.InvalidRoleException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum UserAuthorities {
    USER(0),
    ADMIN(1),
    SUPER_ADMIN(2),

    CAN_CREATE_PERSONAL_LOCATION(3),
    CAN_CREATE_POINT_OF_CONTACT(4);

    @Getter
    private final int code;

    public static UserAuthorities fromCode(int code) {
        for (UserAuthorities authorities : values()) {
            if (authorities.code == code) {
                return authorities;
            }
        }
        throw new InvalidRoleException("user.authorities.code.invalid", code);
    }

    public static UserAuthorities fromString(String roleString) {
        String correctedRoleString = roleString.replaceAll("[-\\s]", "_");
        for (UserAuthorities authorities : values()) {
            if (authorities.toString().equalsIgnoreCase(correctedRoleString)) {
                return authorities;
            }
        }
        throw new InvalidRoleException("user.authorities.string.invalid", roleString);
    }
}
