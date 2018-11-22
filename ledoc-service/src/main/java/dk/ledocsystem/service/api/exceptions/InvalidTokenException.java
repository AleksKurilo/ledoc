package dk.ledocsystem.service.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidTokenException extends LedocException {

    public InvalidTokenException(String message, Object... params) {
        super(message, params);
    }
}
