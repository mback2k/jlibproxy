package de.uxnr.proxy;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

@SuppressWarnings("restriction")
public class Response extends Request {
	protected Headers responseHeaders;

	protected Response(HttpExchange httpExchange) {
		super(httpExchange);
		this.responseHeaders = httpExchange.getResponseHeaders();
	}
	
	protected void populate(Map<String, List<String>> headers) {
		for (Entry<String, List<String>> header : headers.entrySet()) {
			if (header.getKey() != null) {
				this.responseHeaders.put(header.getKey(), header.getValue());
			}
		}
	}
}
