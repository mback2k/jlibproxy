package de.uxnr.proxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;

@SuppressWarnings("restriction")
public class ResponseHandler {
	protected static void rewriteResponse(Response response, List<HostRewriter> hostRewriters) throws URISyntaxException {
		StringBuilder requestMethod_SB = new StringBuilder(response.requestMethod);
		StringBuilder requestURI_SB = new StringBuilder(response.requestURI.toString());
		Headers requestHeaders = new Headers(response.requestHeaders);
		Headers responseHeaders = new Headers(response.responseHeaders);

		for (HostRewriter hostRewriter : hostRewriters) {
			try {
				hostRewriter.rewriteResponse(requestMethod_SB, requestURI_SB, requestHeaders, responseHeaders);
			} catch (IOException e) {
				continue;
			}
		}

		response.requestURI = new URI(requestURI_SB.toString());
		response.requestMethod = requestMethod_SB.toString();
	}

	protected static byte[] processResponse(HttpExchange httpExchange, HttpURLConnection connection, Response response) throws IOException {
		int responseCode = 500;
		try {
			responseCode = Integer.parseInt(response.responseHeaders.getFirst(null).split(" ")[1]);
		} catch (NumberFormatException e) {
			responseCode = connection.getResponseCode();
		}

		long contentLength = connection.getContentLength();
		if (contentLength < 0)
			contentLength = 0;
		else if (contentLength == 0)
			contentLength = -1;

		response.responseHeaders.remove(null);
		httpExchange.sendResponseHeaders(responseCode, contentLength);

		InputStream remoteInput = connection.getInputStream();
		OutputStream localOutput = httpExchange.getResponseBody();
		ByteArrayOutputStream bufferOutput = new ByteArrayOutputStream();

		int size = Math.max(Math.min(remoteInput.available(), 65536), 1024);
		int length = -1;

		byte[] data = new byte[size];
		while ((length = remoteInput.read(data)) != -1) {
			localOutput.write(data, 0, length);
			bufferOutput.write(data, 0, length);
		}

		remoteInput.close();
		localOutput.close();
		bufferOutput.close();

		return bufferOutput.toByteArray();
	}

	protected static void handleResponse(Response response, List<HostHandler> hostHandlers, byte[] body) throws IOException {
		ByteArrayInputStream bufferInput = new ByteArrayInputStream(body);
		Headers requestHeaders = new Headers(response.requestHeaders);
		Headers responseHeaders = new Headers(response.responseHeaders);

		for (HostHandler hostHandler : hostHandlers) {
			try {
				hostHandler.handleResponse(response.requestMethod, response.requestURI, requestHeaders, responseHeaders, bufferInput);
			} catch (IOException e) {
				continue;
			}
		}

		bufferInput.close();
	}
}
