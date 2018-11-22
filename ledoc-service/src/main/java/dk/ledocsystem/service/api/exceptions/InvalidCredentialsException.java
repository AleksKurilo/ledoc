package dk.ledocsystem.service.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidCredentialsException extends LedocException {

    public InvalidCredentialsException(String messageKey) {
        super(messageKey);
    }
}
