package de.uxnr.proxy;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

@SuppressWarnings("restriction")
public class ProxyHandler implements HttpHandler {
	private final Map<String, HostHandler> hostHandlers = new HashMap<String, HostHandler>();
	private final Map<String, HostRewriter> hostRewriters = new HashMap<String, HostRewriter>();

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		Request request = new Request(httpExchange);
		String host = request.requestURI.getHost();

		HostHandler hostHandler = this.findHostHandler(host);
		HostRewriter hostRewriter = this.findHostRewriter(host);

		if (hostRewriter != null) {
			try {
				HostRequest.rewriteRequest(request, hostRewriter);
			} catch (URISyntaxException e) {
				throw new IOException("Unable to rewrite request URI", e);
			}
		}

		URL url = request.requestURI.toURL();
		URLConnection conn = url.openConnection();

		HttpURLConnection connection = null;
		if (conn instanceof URLConnection) {
			connection = (HttpURLConnection) conn;
		} else {
			throw new IOException("Unsupported non-HTTP connection");
		}

		byte[] requestBody = HostRequest.processRequest(httpExchange, connection, request);

		if (hostHandler != null) {
			HostRequest.handleRequest(request, hostHandler, requestBody);
		}

		Response response = new Response(httpExchange);
		response.populate(connection.getHeaderFields());

		if (hostRewriter != null) {
			try {
				HostResponse.rewriteResponse(response, hostRewriter);
			} catch (URISyntaxException e) {
				throw new IOException("Unable to rewrite request URI", e);
			}
		}

		byte[] responseBody = HostResponse.processResponse(httpExchange, connection, response);

		if (hostHandler != null) {
			HostResponse.handleResponse(response, hostHandler, responseBody);
		}

		httpExchange.close();
	}

	private synchronized HostHandler findHostHandler(String host) {
		for (Entry<String, HostHandler> handler : this.hostHandlers.entrySet()) {
			if (host.matches(handler.getKey())) {
				return handler.getValue();
			}
		}
		return null;
	}

	private synchronized HostRewriter findHostRewriter(String host) {
		for (Entry<String, HostRewriter> rewriter : this.hostRewriters.entrySet()) {
			if (host.matches(rewriter.getKey())) {
				return rewriter.getValue();
			}
		}
		return null;
	}

	protected synchronized void addHostHandler(String host, HostHandler hostHandler) {
		this.hostHandlers.put(host, hostHandler);
	}

	protected synchronized void removeHostHandler(String host) {
		this.hostHandlers.remove(host);
	}

	protected synchronized void addHostRewriter(String host, HostRewriter hostRewriter) {
		this.hostRewriters.put(host, hostRewriter);
	}

	protected synchronized void removeHostRewriter(String host) {
		this.hostRewriters.remove(host);
	}
}
