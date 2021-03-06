package dk.ledocsystem.service.api.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Indicates that review of some object cannot be performed.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ReviewNotApplicableException extends LedocException {

    public ReviewNotApplicableException(String messageKey, Object... params) {
        super(messageKey, params);
    }
}
