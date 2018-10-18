package dk.ledocsystem.ledoc.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.SmartValidator;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Lazy})
public abstract class BaseValidator<T> {

    @Autowired
    protected MessageSource messageSource;
    @Autowired
    private SmartValidator smartValidator;

    public abstract void validate(T target);

    protected Map<String, List<String>> getBasicValidation(T target) {
        Errors errors = new BeanPropertyBindingResult(target, target.getClass().getName());
        smartValidator.validate(target, errors);
        return errors.getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(FieldError::getField,
                        Collectors.mapping(
                                fieldError -> messageSource.getMessage(fieldError, LocaleContextHolder.getLocale()), Collectors.toList()
                        )));
    }


}
