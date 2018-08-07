package dk.ledocsystem.ledoc.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRoleNameException extends IllegalArgumentException {

    public InvalidRoleNameException(String roleName) {
        super(roleName + " is unknown UserAuthorities constant");
    }
}
