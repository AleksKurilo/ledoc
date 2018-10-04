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
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

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
    public ResponseEntity<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, Locale locale) {
        BindingResult result = ex.getBindingResult();
        Map<String, String> errorMap = result.getFieldErrors()
                .stream()
                .collect(Collectors.toMap(FieldError::getField, (error) -> messageSource.getMessage(error, locale)));
        return ResponseEntity.badRequest().contentType(MediaType.APPLICATION_JSON).body(errorMap);
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
