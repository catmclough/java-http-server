package javaserver.responders;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

import javaserver.DirectoryHandler;
import javaserver.HTTPStatusCode;
import javaserver.Request;
import javaserver.RequestParser;
import javaserver.Response;

public class PartialResponder extends FileResponder {

	private int contentLength;

	public PartialResponder(String[] supportedMethods, File publicDir) {
		super(supportedMethods, publicDir);
	}

	public Response getResponse(Request request) {
        return new Response.ResponseBuilder(getStatusLine(request))
          .body(getBody(request))
          .header(getHeader())
          .build();
	}

	@Override
	public String getStatusLine(Request request) {
		return HTTPStatusCode.TWO_OH_SIX.getStatusLine();
	}

	private String getHeader() {
		return "Content-Length: " + this.contentLength;
	}

	@Override
	protected String getBody(Request request) {
		File thisFile = new File(DirectoryHandler.getPublicDirectoryPath() + getFileName(request));
		int fileLength = (int) thisFile.length();
		int[] range;

		try {
			range = getRequestedByteRange(RequestParser.getPartialRange(request.getData()), fileLength);
		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Invalid range of bytes requested.");
			throw e;
		}

		byte[] fileContents = new byte[fileLength];

		try {
			FileInputStream fileInput = new FileInputStream(thisFile);
			fileInput.read(fileContents);
			fileInput.close();
		} catch (IOException e) {
			System.out.println("Unable to read from file with input stream");
			e.printStackTrace();
		}

		int startOfRange = range[0];
		int endOfRange = range[1];
		byte[] partialContent = Arrays.copyOfRange(fileContents, startOfRange, endOfRange);
		this.contentLength = partialContent.length;
		return new String(partialContent);
	}

	private int[] getRequestedByteRange(String requestData, int fileLength) {
		String rawRange = requestData.split("=")[1];
		String[] rangeData = getRangeData(rawRange);
		return getRange(rangeData, fileLength);
	}

	private String[] getRangeData(String range) {
		return range.trim().split("-");
	}

	private int[] getRange(String[] range, int fileLength) {
		int rangeStart = 0;
		int rangeEnd = fileLength;

		if (rangeHasNoEnd(range)) {
			rangeStart = getInt(range, 0);
		} else if (rangeHasNoBeginning(range)) {
			rangeStart = fileLength - getInt(range, 1);
		} else {
			rangeStart = getInt(range, 0);
			rangeEnd = getInt(range, 1) + 1; //add one to make range exclusive
		}

		return new int[] {rangeStart, rangeEnd};
	}

	private boolean rangeHasNoEnd(String[] range) {
		return range.length == 1;
	}

	private boolean rangeHasNoBeginning(String[] range) {
		return range[0].isEmpty();
	}

	private int getInt(String[] range, int index) {
		try {
			return Integer.parseInt(range[index]);
		} catch (NumberFormatException e) {
			System.out.println("Invalid range given in partial request");
			return -1;
		}
	}
}
