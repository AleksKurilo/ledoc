package dk.ledocsystem.ledoc.exceptions;

import lombok.Getter;

/**
 * The base class of all system exceptions that would otherwise extend {@link RuntimeException}.
 */
@Getter
public class LedocException extends RuntimeException {

    private final String messageKey;
    private final Object[] params;

    public LedocException(String messageKey, Object... params) {
        this.messageKey = messageKey;
        this.params = params;
    }

}
