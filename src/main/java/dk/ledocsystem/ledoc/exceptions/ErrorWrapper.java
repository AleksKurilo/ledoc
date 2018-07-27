package dk.ledocsystem.ledoc.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorWrapper {

	@JsonProperty
	private String message;

	static ErrorWrapper from(Throwable th) {
		return new ErrorWrapper(getRootSafely(th).getMessage());
	}

	static ErrorWrapper from(String errorMessage) {
		return new ErrorWrapper(errorMessage);
	}

	static String logMessageFrom(Throwable th) {
		String[] rootCauseStackTrace = ExceptionUtils.getRootCauseStackTrace(th);
		return StringUtils.join(rootCauseStackTrace, System.lineSeparator());
	}

	private static Throwable getRootSafely(Throwable th) {
		return (th.getCause() != null) ? ExceptionUtils.getRootCause(th) : th;
	}

}
