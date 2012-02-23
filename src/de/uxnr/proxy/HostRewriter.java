package de.uxnr.proxy;

import java.io.IOException;

public interface HostRewriter {
	public void rewriteRequest(StringBuilder requestMethod, StringBuilder requestURI, Headers requestHeaders) throws IOException;
	public void rewriteResponse(StringBuilder requestMethod, StringBuilder requestURI, Headers requestHeaders, Headers responseHeaders) throws IOException;
}
