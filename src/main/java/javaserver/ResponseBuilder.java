package javaserver;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;

public class ResponseBuilder {
	private String[] OK_ROOT_REQUESTS = {"GET"};
	private String[] OK_FORM_REQUESTS = {"GET", "POST", "PUT"};
	private String[] OK_METHOD_OPTIONS = {"GET", "HEAD", "POST", "OPTIONS", "PUT"};

	protected HashMap<String, String> responseParts;
	protected HashMap<String, String> responseCodes;
	protected HashMap<String, String[]> okRequests;

	ResponseBuilder() {
		this.responseParts = new HashMap<String, String>();
		this.okRequests = new HashMap<String, String[]>();
		setOKRequests();

		this.responseCodes = new HashMap<String, String>();
		setResponseCodes();
	}

	private void setOKRequests() {
		okRequests.put("/", OK_ROOT_REQUESTS);
		okRequests.put("/form", OK_FORM_REQUESTS);
		okRequests.put("/method_options", OK_METHOD_OPTIONS);
	}

	private void setResponseCodes() {
		responseCodes.put("200", "HTTP/1.1 200 OK");
		responseCodes.put("404", "HTTP/1.1 404 Not Found");
	}

	public String getResponse(HashMap<String, String> request) {
		setResponseParts(request);
		String response = responseParts.get("Response Code") + System.lineSeparator();
		response += responseParts.get("Header") + System.lineSeparator();
		response += System.lineSeparator();
		response += responseParts.get("Body") + System.lineSeparator();
		return response;
	}

	private void setResponseParts(HashMap<String, String> request) {
		this.responseParts.put("Response Code", getResponseCode(request));
		this.responseParts.put("Header", getResponseHeader(request));
		this.responseParts.put("Body", getResponseBody(request));
	}

	private String getResponseCode(HashMap<String, String> request) {
		if (isOK(request)) {
			return responseCodes.get("200");
		} else {
			return responseCodes.get("404");
		}
	}
		
	private boolean isOK(HashMap<String, String> request) {
		String[] methodOptions = okRequests.get(request.get("URI"));
		String requestType = request.get("Type");
		if (methodOptions != null && (Arrays.asList(methodOptions).contains(requestType))) {
			return true;
		} else if (hasVariableParams(request.get("URI"))) {
			return true;
		} else {
			return false;
		}
	}
	
	private boolean hasVariableParams(String URI) {
		return URI.contains("/parameters?");
	}

	private String getResponseHeader(HashMap<String, String> request) {
		String header = new String();
		if (request.get("URI").equals("/method_options")) {
			header += "Allow: ";
			header += String.join(",", okRequests.get("/method_options"));
		}
		return header;
	}
	
	private String getResponseBody(HashMap<String, String> request) {
		String body = new String();
		if (hasVariableParams(request.get("URI"))) {
			String params = request.get("URI").split("/parameters?.")[1];
			params = params.replace("=", " = ");
			String[] allParams = params.split("&");
			for (int i=0; i<allParams.length; i++) {
				body += decode(allParams[i]) + System.lineSeparator();
			}
		}
		return body;
	}
	
	private String decode(String parameter) {
		try {
			parameter = java.net.URLDecoder.decode(parameter, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			System.out.println("ResponseBuilder could not decode one or more of the request's parameters");
		}
		return parameter;
	}
}
