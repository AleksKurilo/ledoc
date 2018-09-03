package dk.ledocsystem.ledoc.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RoleAlreadyExistsException extends LedocException {
    public RoleAlreadyExistsException(String msg, Object... params) {
        super(msg, params);
    }
}
