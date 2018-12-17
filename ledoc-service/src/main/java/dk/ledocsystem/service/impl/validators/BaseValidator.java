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

import java.util.Collections;
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

    /**
     * Validates {@code target} against annotations using Spring's own {@link SmartValidator}.
     *
     * @param target           Validating object
     * @param validationGroups Validation groups
     */
    public void validate(T target, Class<?>... validationGroups) {
        validate(target, Collections.emptyMap(), validationGroups);
    }

    /**
     * Validates {@code target} against annotations using Spring's own {@link SmartValidator}.
     *
     * @param target           Validating object
     * @param params           Any parameters required to validate target
     * @param validationGroups Validation groups
     */
    public void validate(T target, Map<String, Object> params, Class<?>... validationGroups) {
        Errors errors = new BeanPropertyBindingResult(target, target.getClass().getName());
        smartValidator.validate(target, errors, (Object[]) validationGroups);

        Map<String, List<String>> messages = errors.getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(FieldError::getField,
                        Collectors.mapping(
                                fieldError -> messageSource.getMessage(fieldError, getLocale()), Collectors.toList()
                        )));
        validateInner(target, params, messages);
        if (!messages.isEmpty()) {
            throw new ValidationDtoException(messages);
        }
    }

    /**
     * Hook for extenders to customize validation.
     *
     * @param target   Validating object
     * @param params   Any parameters required to validate target
     * @param messages Mapping between field name and list of validation error messages.
     *                 Should be used to add error messages
     */
    protected void validateInner(T target, Map<String, Object> params, Map<String, List<String>> messages) {
    }

    /**
     * @return the Locale associated with the current thread, if any, or the system default Locale otherwise.
     */
    protected final Locale getLocale() {
        return LocaleContextHolder.getLocale();
    }
}
