package dk.ledocsystem.ledoc.exceptions;

import lombok.Getter;

import java.util.List;
import java.util.Map;

public class ValidationDtoException extends RuntimeException {

    @Getter
    private Map<String, List<String>> errors;

    public ValidationDtoException(Map<String, List<String>> errors) {
        this.errors = errors;
    }
}
