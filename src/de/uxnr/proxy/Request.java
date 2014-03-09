package de.uxnr.proxy;

import java.net.URI;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public class Request {
  protected String requestMethod;
  protected URI requestURI;
  protected Headers requestHeaders;

  protected Request(HttpExchange httpExchange) {
    this.requestMethod = httpExchange.getRequestMethod();
    this.requestURI = httpExchange.getRequestURI();
    this.requestHeaders = new Headers();
    this.requestHeaders.putAll(httpExchange.getRequestHeaders());
  }
}
