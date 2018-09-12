package dk.ledocsystem.ledoc.excel.exception;

import dk.ledocsystem.ledoc.exceptions.LedocException;

public class InvalidModuleException extends LedocException {

    public InvalidModuleException(String msg, Object... params) {
        super(msg, params);
    }
}
