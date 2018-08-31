package dk.ledocsystem.ledoc.exceptions;

public class InvalidTokenException extends LedocException {

    public InvalidTokenException(String message, Object... params) {
        super(message, params);
    }
}
