package dk.ledocsystem.service.impl.validators;

import dk.ledocsystem.service.api.exceptions.ValidationDtoException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.SmartValidator;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BaseValidator<T> {

    @Autowired
    protected MessageSource messageSource;
    @Autowired
    private SmartValidator smartValidator;

    public void validate(T target) {
        Errors errors = new BeanPropertyBindingResult(target, target.getClass().getName());
        smartValidator.validate(target, errors);

        Map<String, List<String>> messages = errors.getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(FieldError::getField,
                        Collectors.mapping(
                                fieldError -> messageSource.getMessage(fieldError, getLocale()), Collectors.toList()
                        )));
        validateInner(target, messages);
        if (!messages.isEmpty()) {
            throw new ValidationDtoException(messages);
        }
    }

    protected void validateInner(T target, Map<String, List<String>> messages) {
    }

    protected final Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }

}
