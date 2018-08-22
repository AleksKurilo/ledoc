package dk.ledocsystem.ledoc.exceptions;

import lombok.RequiredArgsConstructor;
import org.pmw.tinylog.Logger;
import org.springframework.context.MessageSource;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@ControllerAdvice
@RequiredArgsConstructor
class RestExceptionHandler {

    private static final String UNEXPECTED_ERROR = "Exception.unexpected";
    private final MessageSource messageSource;

    @ExceptionHandler(LedocException.class)
    public ResponseEntity<RestResponse> handleLedocException(LedocException ex, Locale locale) {
        String errorMessage = messageSource.getMessage(ex.getMessageKey(), ex.getParams(), locale);
        return handleExceptionInternal(ex, new RestResponse(errorMessage));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, Locale locale) {
        BindingResult result = ex.getBindingResult();
        List<String> errorMessages = result.getAllErrors()
                .stream()
                .map(objectError -> messageSource.getMessage(objectError, locale))
                .collect(Collectors.toList());
        return new ResponseEntity<>(new RestResponse(errorMessages), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestResponse> handleException(Exception ex, Locale locale) {
        String errorMessage = messageSource.getMessage(UNEXPECTED_ERROR, null, locale);
        return handleExceptionInternal(ex, new RestResponse(errorMessage));
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
