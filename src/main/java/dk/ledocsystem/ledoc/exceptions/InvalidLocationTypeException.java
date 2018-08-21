package dk.ledocsystem.ledoc.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidLocationTypeException extends LedocException {

    public InvalidLocationTypeException(String locationType) {
        super("location.type.not.found", locationType);
    }
}
