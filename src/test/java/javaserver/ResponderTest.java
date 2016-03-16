package javaserver;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class ResponderTest {
	Responder testResponseBuilder;
	SocketWriter writer;
	Responder responder;

	String codedURI = "/parameters?variable_1=Operators%20%3C%2C%20%3E%2C%20%3D%2C%20!%3D%3B%20%2B%2C%20-%2C%20*%2C%20%26%2C%20%40%2C%20%23%2C%20%24%2C%20%5B%2C%20%5D%3A%20%22is%20that%20all%22%3F&variable_2=stuff";

	Request getRoot = new Request("GET", "/");
	Request getForm = new Request("GET", "/form");
	Request postForm = new Request("POST", "/form");
	Request putForm = new Request("PUT", "/form");
	Request getBogusRoute = new Request("GET", "/foo");
	Request getMethodOptions = new Request("GET", "/method_options");
	Request getCodedParams = new Request("GET", codedURI);

	String twoHundred = HTTPStatusCodes.TWO_HUNDRED;
	String fourOhFour = HTTPStatusCodes.FOUR_OH_FOUR;


	@Before
	public void setUp() {
		App.configureRoutes();
	}

	@Test
	public void testCreatesReponseWithResponseCode() {
		Responder responder = new Responder(getRoot);
		Response response = responder.getResponse();
		assertNotNull(response.getResponseCode());
	}

	@Test
	public void testCreatesReponseWithHeader() {
		Responder responder = new Responder(getMethodOptions);
		Response response = responder.getResponse();
		assertNotNull(response.getHeader());
	}

	@Test
	public void testCreatesReponseWithBody() {
		Responder responder = new Responder(getCodedParams);
		Response response = responder.getResponse();
		assertNotNull(response.getBody());
	}

	@Test
	public void testGetRootResponseCode() throws IOException {
		Responder responder = new Responder(getRoot);
		Response response = responder.getResponse();
		assertEquals(response.getResponseCode(), twoHundred);
	}

	@Test
	public void testGetFormResponseCode() throws IOException {
		Responder responder = new Responder(getForm);
		Response response = responder.getResponse();
		assertEquals(response.getResponseCode(), twoHundred);
	}

	@Test
	public void testPostFormResponseCode() throws IOException {
		Responder responder = new Responder(postForm);
		Response response = responder.getResponse();
		assertEquals(response.getResponseCode(), twoHundred);
	}

	@Test
	public void testPutFormResponseCode() throws IOException {
		Responder responder = new Responder(putForm);
		Response response = responder.getResponse();
		assertEquals(response.getResponseCode(), twoHundred);
	}

	@Test
	public void testFourOhFourResponseCode() throws IOException {
		Responder responder = new Responder(getBogusRoute);
		Response response = responder.getResponse();
		assertEquals(response.getResponseCode(), fourOhFour);
	}

	@Test
	public void testMethodOptionsResponseCode() {
		Responder responder = new Responder(getMethodOptions);
		Response response = responder.getResponse();
		assertEquals(response.getResponseCode(), twoHundred);
	}

	@Test
	public void testMethodOptionsHeader() throws IOException {
		Responder responder = new Responder(getMethodOptions);
		Response response = responder.getResponse();
		String methodOptionsHeader = "Allow: GET,HEAD,POST,OPTIONS,PUT";
		assertEquals(response.getHeader(), methodOptionsHeader);
	}

	@Test
	public void testDecodedParamsInResponseBody() {
		String decodedParamOne = "variable_1 = Operators <, >, =, !=; +, -, *, &, @, #, $, [, ]: \"is that all\"?";
		String decodedParamTwo = "variable_2 = stuff";

		Responder responder = new Responder(getCodedParams);
		Response response = responder.getResponse();
		String responseBody = response.getBody();

		assertTrue(responseBody.contains(decodedParamOne));
		assertTrue(responseBody.contains(decodedParamTwo));
	}

	@Test
	public void testCodedParamsGets200Response() {
		Responder responder = new Responder(getCodedParams);
		Response response = responder.getResponse();
		assertEquals(response.getResponseCode(), twoHundred);
	}
}
