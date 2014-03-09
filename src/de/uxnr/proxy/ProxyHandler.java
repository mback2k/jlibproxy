package de.uxnr.proxy;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import de.uxnr.proxy.util.ListMap;

public class ProxyHandler implements HttpHandler {
  private final ListMap<String, HostHandler> hostHandlers = new ListMap<String, HostHandler>();
  private final ListMap<String, HostRewriter> hostRewriters = new ListMap<String, HostRewriter>();

  @Override
  public void handle(HttpExchange httpExchange) throws IOException {
    Request request = new Request(httpExchange);
    String host = request.requestURI.getHost();

    List<HostHandler> hostHandlers = this.findHostHandlers(host);
    List<HostRewriter> hostRewriters = this.findHostRewriters(host);

    if (hostRewriters.size() > 0) {
      try {
        RequestHandler.rewriteRequest(request, hostRewriters);
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

    byte[] requestBody = RequestHandler.processRequest(httpExchange, connection, request);

    if (hostHandlers.size() > 0) {
      RequestHandler.handleRequest(request, hostHandlers, requestBody);
    }

    Response response = new Response(httpExchange);
    response.populate(connection.getHeaderFields());

    if (hostRewriters.size() > 0) {
      try {
        ResponseHandler.rewriteResponse(response, hostRewriters);
      } catch (URISyntaxException e) {
        throw new IOException("Unable to rewrite request URI", e);
      }
    }

    byte[] responseBody = ResponseHandler.processResponse(httpExchange, connection, response);

    if (hostHandlers.size() > 0) {
      ResponseHandler.handleResponse(response, hostHandlers, responseBody);
    }

    httpExchange.close();
  }

  private synchronized List<HostHandler> findHostHandlers(String host) {
    List<HostHandler> handlers = new Vector<HostHandler>();
    for (Entry<String, List<HostHandler>> handler : this.hostHandlers.entrySet()) {
      if (host.matches(handler.getKey())) {
        handlers.addAll(handler.getValue());
      }
    }
    return handlers;
  }

  private synchronized List<HostRewriter> findHostRewriters(String host) {
    List<HostRewriter> rewriters = new Vector<HostRewriter>();
    for (Entry<String, List<HostRewriter>> rewriter : this.hostRewriters.entrySet()) {
      if (host.matches(rewriter.getKey())) {
        rewriters.addAll(rewriter.getValue());
      }
    }
    return rewriters;
  }

  protected synchronized void addHostHandler(String host, HostHandler hostHandler) {
    this.hostHandlers.add(host, hostHandler);
  }

  protected synchronized void removeHostHandler(String host) {
    this.hostHandlers.remove(host);
  }

  protected synchronized void addHostRewriter(String host, HostRewriter hostRewriter) {
    this.hostRewriters.add(host, hostRewriter);
  }

  protected synchronized void removeHostRewriter(String host) {
    this.hostRewriters.remove(host);
  }
}
