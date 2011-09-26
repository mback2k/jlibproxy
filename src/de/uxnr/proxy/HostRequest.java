package de.uxnr.proxy;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

@SuppressWarnings("restriction")
public class HostRequest {
	private HttpExchange httpExchange;
	private HttpURLConnection connection;
	private HostHandler hostHandler;
	
	protected HostRequest(HttpExchange httpExchange, HttpURLConnection connection, HostHandler hostHandler) {
		this.httpExchange = httpExchange;
		this.connection = connection;
		this.hostHandler = hostHandler;
	}

	public void process() throws IOException {
		String requestMethod = this.httpExchange.getRequestMethod();
		URI requestURI = this.httpExchange.getRequestURI();
		Headers requestHeaders = this.httpExchange.getRequestHeaders();
		
		for (String header : requestHeaders.keySet()) {
			if (header != null && !header.toLowerCase().startsWith("proxy-")) {
				this.connection.setRequestProperty(header, requestHeaders.getFirst(header));
			}
		}
		
		this.connection.setRequestMethod(requestMethod);
		this.connection.setDefaultUseCaches(false);
		this.connection.setInstanceFollowRedirects(false);
		this.connection.setDoInput(true);
		
		ByteArrayOutputStream bufferOutput = new ByteArrayOutputStream();
		
		if (requestMethod.equalsIgnoreCase("POST")) {
			this.connection.setDoOutput(true);
			this.connection.connect();
			
			InputStream localInput = this.httpExchange.getRequestBody();
			OutputStream remoteOutput = this.connection.getOutputStream();
			
			int length = -1;
			byte[] data = new byte[1024];
			while ((length = localInput.read(data)) != -1) {
				remoteOutput.write(data, 0, length);
				bufferOutput.write(data, 0, length);
			}
			
			remoteOutput.close();
			bufferOutput.close();
		} else {
			this.connection.connect();
		}
		
		if (this.hostHandler != null) {
			ByteArrayInputStream body = new ByteArrayInputStream(bufferOutput.toByteArray());
			Map<String, String> headers = new HashMap<String, String>();
			for (String header : requestHeaders.keySet()) {
				if (header != null) {
					headers.put(header.toLowerCase(), requestHeaders.getFirst(header));
				}
			}
			this.hostHandler.handleRequest(requestMethod, requestURI, headers, body);
		}
	}
}
