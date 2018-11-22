package dk.ledocsystem.service.api.exceptions;

public class InvalidModuleException extends LedocException {

    public InvalidModuleException(String msg, Object... params) {
        super(msg, params);
    }
}
