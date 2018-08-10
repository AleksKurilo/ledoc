package dk.ledocsystem.ledoc.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.List;

public class RestResponse {

	@JsonProperty
	private String message;
	@JsonProperty
	private List<String> messages;

	public RestResponse(List<String> messages) {
		this.messages = messages;
	}

	public RestResponse(String message) {
		this.message = message;
	}

	static String logMessageFrom(Throwable th) {
		String[] rootCauseStackTrace = ExceptionUtils.getRootCauseStackTrace(th);
		return StringUtils.join(rootCauseStackTrace, System.lineSeparator());
	}

}
