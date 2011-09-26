package de.uxnr.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

public interface HostHandler {
	public void handleRequest(String requestMethod, URI requestURI, Map<String, String> requestHeaders, InputStream body) throws IOException;
	public void handleResponse(String requestMethod, URI requestURI, Map<String, String> requestHeaders, Map<String, String> responseHeaders, InputStream body) throws IOException;
}
