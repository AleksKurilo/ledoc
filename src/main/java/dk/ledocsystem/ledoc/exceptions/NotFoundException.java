package dk.ledocsystem.ledoc.exceptions;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends IllegalStateException {

    public NotFoundException(Class<?> entity, Object... params) {
        super(String.format("Entity " + entity.getSimpleName() + " with %s not found", StringUtils.join(params, ",")));
    }

    public NotFoundException(Class<?> entity, Long id) {
        super(String.format("Entity " + entity.getSimpleName() + " with id %d not found", id));
    }
}
