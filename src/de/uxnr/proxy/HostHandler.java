package de.uxnr.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public interface HostHandler {
	public void handleRequest(String requestMethod, URI requestURI, Headers requestHeaders, InputStream body) throws IOException;

	public void handleResponse(String requestMethod, URI requestURI, Headers requestHeaders, Headers responseHeaders, InputStream body) throws IOException;
}
