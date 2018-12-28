package dk.ledocsystem.api.exceptions;

import dk.ledocsystem.service.api.exceptions.LedocException;
import dk.ledocsystem.service.api.exceptions.ValidationDtoException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.pmw.tinylog.Logger;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Locale;

import static dk.ledocsystem.service.impl.constant.ErrorMessageKey.UNEXPECTED_ERROR;

@ControllerAdvice
@RequiredArgsConstructor
class RestExceptionHandler extends ResponseEntityExceptionHandler implements AsyncUncaughtExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(LedocException.class)
    public ResponseEntity<RestResponse> handleLedocException(LedocException ex, Locale locale) {
        String errorMessage = messageSource.getMessage(ex.getMessageKey(), ex.getParams(), locale);
        return handleExceptionInternal(ex, new RestResponse(Collections.singletonList(errorMessage)));
    }

    @ExceptionHandler(ValidationDtoException.class)
    public ResponseEntity<RestResponse> handleValidationException(ValidationDtoException ex) {
        return new ResponseEntity<>(new RestResponse(ex.getErrors()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RestResponse> handleAccessDenied(AccessDeniedException ex) {
        return handleExceptionInternal(ex, new RestResponse(Collections.singletonList(ex.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResponse> handleException(Exception ex, Locale locale) {
        String errorMessage = messageSource.getMessage(UNEXPECTED_ERROR, null, locale);
        return handleExceptionInternal(ex, new RestResponse(Collections.singletonList(errorMessage)));
    }

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        Logger.error(logMessageFrom(ex));
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers,
                                                             HttpStatus status, WebRequest request) {
        String message = ExceptionUtils.getRootCause(ex).getMessage();
        body = new RestResponse(Collections.singletonList(message));
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    private ResponseEntity<RestResponse> handleExceptionInternal(Exception ex, RestResponse error) {
        Logger.error(logMessageFrom(ex));
        HttpStatus status = resolveResponseStatus(ex);
        return ResponseEntity
                .status(status)
                .body(error);
    }

    private HttpStatus resolveResponseStatus(Exception exception) {
        ResponseStatus annotation = AnnotatedElementUtils.findMergedAnnotation(exception.getClass(), ResponseStatus.class);
        return (annotation != null) ? annotation.value() : HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String logMessageFrom(Throwable th) {
        String[] rootCauseStackTrace = ExceptionUtils.getRootCauseStackTrace(th);
        return String.join(System.lineSeparator(), rootCauseStackTrace);
    }
}
