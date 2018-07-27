package dk.ledocsystem.ledoc.exceptions;

import java.util.stream.Collectors;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorWrapper> handleException(Exception ex) {
		logger.error(ErrorWrapper.logMessageFrom(ex));
		ErrorWrapper error = ErrorWrapper.from(ex);
		HttpStatus status = resolveResponseStatus(ex);
		return ResponseEntity
				.status(status)
				.body(error);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
	                                                              HttpHeaders headers, HttpStatus status, WebRequest request) {
		String errorMsg = ex.getBindingResult()
				.getAllErrors()
				.stream()
				.map(ObjectError::getDefaultMessage)
				.collect(Collectors.joining(System.lineSeparator()));

		return super.handleExceptionInternal(ex, ErrorWrapper.from(errorMsg), headers, status, request);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body,
	                                                         HttpHeaders headers, HttpStatus status, WebRequest request) {
		logger.error(ErrorWrapper.logMessageFrom(ex));
		return super.handleExceptionInternal(ex, ErrorWrapper.from(ex), headers, status, request);
	}

	private HttpStatus resolveResponseStatus(Exception exception) {
		ResponseStatus annotation = AnnotatedElementUtils.findMergedAnnotation(exception.getClass(), ResponseStatus.class);
		return (annotation != null) ? annotation.value() : HttpStatus.INTERNAL_SERVER_ERROR;
	}
}
