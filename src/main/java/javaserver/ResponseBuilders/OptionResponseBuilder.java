package javaserver.ResponseBuilders;

import javaserver.HTTPStatusCode;
import javaserver.Request;
import javaserver.Routes;

public class OptionResponseBuilder extends ResponseBuilder {

	public OptionResponseBuilder(Request request) {
		super(request);
	}

	@Override
	protected void setResponseData() {
		response.setStatusLine(getStatusLine());
		response.setHeader(getResponseHeader());
	}

	@Override
	protected String getStatusLine() {
		HTTPStatusCode responseCode;
		if (requestIsSupported(request.getMethod(), request.getURI())) {
			responseCode = HTTPStatusCode.TWO_HUNDRED;
		} else {
			responseCode = HTTPStatusCode.FOUR_OH_FOUR;
		}
		return responseCode.getStatusLine();
	}

	private String getResponseHeader() {
		String header = new String();
		if (requestIsSupported(request.getMethod(), request.getURI())) {
			header += "Allow: ";
			header += String.join(",", Routes.routeOptions.get(request.getURI()));
		}
		return header;
	}
}
