package dk.ledocsystem.data.exceptions;

import lombok.Getter;

@Getter
public class InvalidRoleException extends RuntimeException {

    private final String messageKey;
    private final Object roleId;

    public InvalidRoleException(String messageKey, Object roleId) {
        this.messageKey = messageKey;
        this.roleId = roleId;
    }
}
