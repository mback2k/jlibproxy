package de.uxnr.proxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;

import com.sun.net.httpserver.HttpExchange;

@SuppressWarnings("restriction")
public class HostRequest {
	protected static void rewriteRequest(Request request, HostRewriter hostRewriter) throws IOException, URISyntaxException {
		StringBuilder requestMethod_SB = new StringBuilder(request.requestMethod);
		StringBuilder requestURI_SB = new StringBuilder(request.requestURI.toString());

		hostRewriter.rewriteRequest(requestMethod_SB, requestURI_SB, new Headers(request.requestHeaders));

		request.requestURI = new URI(requestURI_SB.toString());
		request.requestMethod = requestMethod_SB.toString();
	}

	protected static byte[] processRequest(HttpExchange httpExchange, HttpURLConnection connection, Request request) throws IOException {
		for (String header : request.requestHeaders.keySet()) {
			if (header != null && !header.toLowerCase().startsWith("proxy-")) {
				StringBuilder props = new StringBuilder();
				for (String prop : request.requestHeaders.get(header)) {
					if (props.length() > 0) {
						props.append("; ");
					}
					props.append(prop);
				}
				connection.setRequestProperty(header, props.toString());
			}
		}

		connection.setRequestMethod(request.requestMethod);
		connection.setDefaultUseCaches(false);
		connection.setInstanceFollowRedirects(false);
		connection.setDoInput(true);

		ByteArrayOutputStream bufferOutput = new ByteArrayOutputStream();

		if (request.requestMethod.equalsIgnoreCase("POST")) {
			connection.setDoOutput(true);
			connection.connect();

			InputStream localInput = httpExchange.getRequestBody();
			OutputStream remoteOutput = connection.getOutputStream();

			int size = Math.max(Math.min(localInput.available(), 65536), 1024);
			int length = -1;

			byte[] data = new byte[size];
			while ((length = localInput.read(data)) != -1) {
				remoteOutput.write(data, 0, length);
				bufferOutput.write(data, 0, length);
			}

			remoteOutput.close();
			bufferOutput.close();
		} else {
			connection.connect();
		}

		return bufferOutput.toByteArray();
	}

	protected static void handleRequest(Request request, HostHandler hostHandler, byte[] body) throws IOException {
		ByteArrayInputStream bufferInput = new ByteArrayInputStream(body);

		hostHandler.handleRequest(request.requestMethod, request.requestURI, new Headers(request.requestHeaders), bufferInput);

		bufferInput.close();
	}
}
