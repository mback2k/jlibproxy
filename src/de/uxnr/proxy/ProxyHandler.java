package de.uxnr.proxy;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
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

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		URI requestURI = httpExchange.getRequestURI();

		URL url = requestURI.toURL();
		URLConnection conn = url.openConnection();

		HttpURLConnection connection = null;
		if (conn instanceof URLConnection) {
			connection = (HttpURLConnection) conn;
		} else {
			throw new IOException("Unsupported non-HTTP connection");
		}

		String host = requestURI.getHost();

		HostHandler hostHandler = null;
		for (Entry<String, HostHandler> handler : this.hostHandlers.entrySet()) {
			if (host.matches(handler.getKey())) {
				try {
					hostHandler = handler.getValue().getClass().newInstance();
				} catch (Exception e) {
					hostHandler = null;
				}
				break;
			}
		}

		HostRequest hostRequest = new HostRequest(httpExchange, connection, hostHandler);
		hostRequest.process();

		HostResponse hostResponse = new HostResponse(httpExchange, connection, hostHandler);
		hostResponse.process();

		httpExchange.close();
	}

	protected synchronized void addHostHandler(String host, HostHandler hostHandler) {
		this.hostHandlers.put(host, hostHandler);
	}

	protected synchronized void removeHostHandler(String host) {
		this.hostHandlers.remove(host);
	}
}
