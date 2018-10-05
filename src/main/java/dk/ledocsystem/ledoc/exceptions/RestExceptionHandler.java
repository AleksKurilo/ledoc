package dk.ledocsystem.ledoc.exceptions;

import lombok.RequiredArgsConstructor;
import org.pmw.tinylog.Logger;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


@ControllerAdvice
@RequiredArgsConstructor
class RestExceptionHandler implements AsyncUncaughtExceptionHandler {

    private static final String UNEXPECTED_ERROR = "Exception.unexpected";
    private final MessageSource messageSource;

    @ExceptionHandler(LedocException.class)
    public ResponseEntity<RestResponse> handleLedocException(LedocException ex, Locale locale) {
        String errorMessage = messageSource.getMessage(ex.getMessageKey(), ex.getParams(), locale);
        return handleExceptionInternal(ex, new RestResponse(Arrays.asList(errorMessage)));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, Locale locale) {
        BindingResult result = ex.getBindingResult();

        Map<String, String> fieldsWithErrors = new HashMap<>();
        result.getFieldErrors().forEach(fieldError -> {
            String field = fieldError.getField();
            if (fieldsWithErrors.containsKey(field)) {
                String error = fieldsWithErrors.get(field) + "," + messageSource.getMessage(fieldError, locale);
                fieldsWithErrors.put(field, error);
            } else {
                fieldsWithErrors.put(field, messageSource.getMessage(fieldError, locale));
            }
        });

        Map<String, Object> jsonErrors = new HashMap<>();
        fieldsWithErrors.entrySet().forEach(s -> {
            if (s.getKey().contains(".")) {
                Map<String, String> nestedObject = new HashMap<>();
                String[] fields = s.getKey().split("\\.");
                nestedObject.put(fields[1], s.getValue());
                jsonErrors.put(fields[0], nestedObject);
            } else {
                jsonErrors.put(s.getKey(), s.getValue());
            }
        });
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(jsonErrors);
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
