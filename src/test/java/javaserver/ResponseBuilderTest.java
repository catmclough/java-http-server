package javaserver;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class ResponseBuilderTest {
	ResponseBuilder testResponseBuilder;
	SocketWriter writer;
	ResponseBuilder responder;
	RequestParser requestParser;

	String codedURI = "/parameters?variable_1=Operators%20%3C%2C%20%3E%2C%20%3D%2C%20!%3D%3B%20%2B%2C%20-%2C%20*%2C%20%26%2C%20%40%2C%20%23%2C%20%24%2C%20%5B%2C%20%5D%3A%20%22is%20that%20all%22%3F&variable_2=stuff";

	Request requestWithCodedParams = new Request("GET", codedURI);
	Request acceptableRequest =  new Request("GET", "/form");
	Request unacceptableRequest = new Request("GET", "/foo");
	Request requestWithOptions = new Request("GET", "/method_options");
	
	String methodOptionsHeader = "Allow: GET,HEAD,POST,OPTIONS,PUT";
	String twoHundred = HTTPStatusCode.TWO_HUNDRED.getStatusLine();
	String fourOhFour= HTTPStatusCode.FOUR_OH_FOUR.getStatusLine();


	@Before
	public void setUp() {
		App.configureRoutes();
	}

	@Test
	public void testCreatesResponseWithResponseCode() {
		ResponseBuilder responder = new ResponseBuilder(acceptableRequest);
		Response response = responder.getResponse();
		assertNotNull(response.getResponseCode());
	}

	@Test
	public void testCreatesResponseWithHeader() {
		ResponseBuilder responder = new ResponseBuilder(acceptableRequest);
		Response response = responder.getResponse();
		assertNotNull(response.getHeader());
	}

	@Test
	public void testCreatesResponseWithBody() {
		ResponseBuilder responder = new ResponseBuilder(acceptableRequest);
		Response response = responder.getResponse();
		assertNotNull(response.getBody());
	}

	@Test
	public void testTwoHundredResponseCode() throws IOException {
		ResponseBuilder responder = new ResponseBuilder(acceptableRequest);
		Response response = responder.getResponse();
		assertEquals(response.getResponseCode(), twoHundred);
	}

	@Test
	public void testFourOhFourResponseCode() throws IOException {
		ResponseBuilder responder = new ResponseBuilder(unacceptableRequest);
		Response response = responder.getResponse();
		assertEquals(response.getResponseCode(), fourOhFour);
	}

	@Test
	public void testOptionsHeader() throws IOException {
		ResponseBuilder responder = new ResponseBuilder(requestWithOptions);
		Response response = responder.getResponse();
		assertEquals(response.getHeader(), methodOptionsHeader);
	}

	@Test
	public void testDecodedParamsInResponseBody() {
		String decodedParamOne = "variable_1 = Operators <, >, =, !=; +, -, *, &, @, #, $, [, ]: \"is that all\"?";
		String decodedParamTwo = "variable_2 = stuff";

		ResponseBuilder responder = new ResponseBuilder(requestWithCodedParams);
		Response response = responder.getResponse();
		String responseBody = response.getBody();

		assertTrue(responseBody.contains(decodedParamOne));
		assertTrue(responseBody.contains(decodedParamTwo));
	}

	@Test
	public void testCodedParamsGets200Response() {
		ResponseBuilder responder = new ResponseBuilder(requestWithCodedParams);
		Response response = responder.getResponse();
		assertEquals(response.getResponseCode(), twoHundred);
	}
}