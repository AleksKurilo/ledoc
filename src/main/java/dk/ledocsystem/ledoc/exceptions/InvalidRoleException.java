package dk.ledocsystem.ledoc.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRoleException extends LedocException {

    public InvalidRoleException(String messageKey, Object locale) {
        super(messageKey, locale);
    }
}
