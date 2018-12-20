package dk.ledocsystem.api.exceptions;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class RestResponse {

	@JsonProperty
	private List<String> messages;

	@JsonProperty
	private Map<String, List<String>> errors;

	public RestResponse(List<String> messages) {
		this.messages = messages;
	}

	public RestResponse(Map<String, List<String>> errors) {
		this.errors = errors;
	}

}
