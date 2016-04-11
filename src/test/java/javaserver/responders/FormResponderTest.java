package javaserver.responders;

import static org.junit.Assert.*;
import org.junit.Test;
import javaserver.HTTPStatusCode;
import javaserver.Request;
import javaserver.RequestParser;
import javaserver.Response;
import javaserver.Routes;

public class FormResponderTest {
	private String formRoute = "/form";
	private String mockPostData = "snack=crackerjack";

	private Request getForm = RequestParser.createRequest("GET " + formRoute);
	private Request postFormWithData = RequestParser.createRequest("POST " + formRoute + "\n\n" + mockPostData);
	private Request putForm = RequestParser.createRequest("PUT " + formRoute);
	private Request deleteForm = RequestParser.createRequest("DELETE " + formRoute);
	private Request invalidRequest = RequestParser.createRequest("PATCH " + formRoute);

	private Responder responder = Routes.getResponder(formRoute);
	private String twoHundred = HTTPStatusCode.TWO_HUNDRED.getStatusLine();

	@Test
	public void testFormResponderCreation() {
	    assertEquals(responder.getClass(), FormResponder.class);
	}

	@Test
	public void testGetFormResponseCode() {
		Response getFormResponse = responder.getResponse(getForm);
		assertEquals(getFormResponse.getResponseCode(), twoHundred);
	}

	@Test
	public void testPostToFormResponseCode() {
		Response postFormResponse = responder.getResponse(postFormWithData);
		assertEquals(postFormResponse.getResponseCode(), twoHundred);
	}

	@Test
	public void testPutFormResponseCode() {
		Response putFormResponse = responder.getResponse(putForm);
		assertEquals(putFormResponse.getResponseCode(), twoHundred);
	}

	@Test
	public void testResponseToDeleteForm() {
		Response deleteFormResponse = responder.getResponse(deleteForm);
		assertEquals(deleteFormResponse.getResponseCode(), twoHundred);
	}

	@Test
	public void testInvalidMethodForm() {
		Response deleteFormResponse = responder.getResponse(invalidRequest);
		assertEquals(deleteFormResponse.getResponseCode(), HTTPStatusCode.FOUR_OH_FOUR.getStatusLine());
	}

	@Test
	public void testFormInitializedWithEmptyDataLine() {
		Response getFormResponse = responder.getResponse(getForm);
		assertTrue(getFormResponse.getBody().isEmpty());
	}

	@Test
	public void testPostDataToForm() {
		Response getFormResponse = responder.getResponse(getForm);
		assertFalse(getFormResponse.getBody().contains(mockPostData));

		responder.getResponse(postFormWithData);
		Response getUpdatedForm = responder.getResponse(getForm);
		assertTrue(getUpdatedForm.getBody().contains(mockPostData));
	}

	@Test
	public void testDeleteForm() {
		Response getFormResponse =  responder.getResponse(getForm);
		assertFalse(getFormResponse.getBody().isEmpty());

		responder.getResponse(deleteForm);
		Response newFormResponse = responder.getResponse(getForm);
		assertTrue(newFormResponse.getBody().isEmpty());
	}
}