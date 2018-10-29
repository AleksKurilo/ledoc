package dk.ledocsystem.ledoc.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidEnumValueException extends LedocException {

    public InvalidEnumValueException(String messageKey, Object... params) {
        super(messageKey, params);
    }
}
