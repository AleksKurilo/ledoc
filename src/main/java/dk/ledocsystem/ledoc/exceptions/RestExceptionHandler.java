package dk.ledocsystem.ledoc.exceptions;

import lombok.RequiredArgsConstructor;
import org.pmw.tinylog.Logger;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static dk.ledocsystem.ledoc.constant.ErrorMessageKey.UNEXPECTED_ERROR;


@ControllerAdvice
@RequiredArgsConstructor
class RestExceptionHandler implements AsyncUncaughtExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(LedocException.class)
    public ResponseEntity<RestResponse> handleLedocException(LedocException ex, Locale locale) {
        String errorMessage = messageSource.getMessage(ex.getMessageKey(), ex.getParams(), locale);
        return handleExceptionInternal(ex, new RestResponse(Arrays.asList(errorMessage)));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, Locale locale) {
        BindingResult result = ex.getBindingResult();
        Map<String, List<String>> fieldsWithErrors = result.getFieldErrors()
                .stream()
                .collect(Collectors.groupingBy(FieldError::getField,
                        Collectors.mapping(
                                fieldError -> messageSource.getMessage(fieldError, locale), Collectors.toList()
                        )));
        return new ResponseEntity<>(new RestResponse(fieldsWithErrors), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ValidationDtoException.class)
    public ResponseEntity<RestResponse> handleValidationException(ValidationDtoException ex) {
        return new ResponseEntity<>(new RestResponse(ex.getErrors()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RestResponse> handleAccessDenied(AccessDeniedException ex) {
        return handleExceptionInternal(ex, new RestResponse(Arrays.asList(ex.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResponse> handleException(Exception ex, Locale locale) {
        String errorMessage = messageSource.getMessage(UNEXPECTED_ERROR, null, locale);
        return handleExceptionInternal(ex, new RestResponse(Arrays.asList(errorMessage)));
    }

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        Logger.error(RestResponse.logMessageFrom(ex));
    }

    private ResponseEntity<RestResponse> handleExceptionInternal(Exception ex, RestResponse error) {
        Logger.error(RestResponse.logMessageFrom(ex));
        HttpStatus status = resolveResponseStatus(ex);
        return ResponseEntity
                .status(status)
                .body(error);
    }

    private HttpStatus resolveResponseStatus(Exception exception) {
        ResponseStatus annotation = AnnotatedElementUtils.findMergedAnnotation(exception.getClass(), ResponseStatus.class);
        return (annotation != null) ? annotation.value() : HttpStatus.INTERNAL_SERVER_ERROR;
    }

}
