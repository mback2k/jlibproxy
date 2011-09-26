package de.uxnr.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;

@SuppressWarnings("restriction")
public class Proxy implements Runnable {
	private ProxyHandler handler;
	private InetSocketAddress address;
	private HttpServer server;
	
	public Proxy(int port) throws IOException {
		this.handler = new ProxyHandler();
		this.address = new InetSocketAddress("localhost", port);
		this.server = HttpServer.create(this.address, 10);
		this.server.createContext("/", this.handler);
	}
	
	public void start() {
		this.server.start();
	}
	
	public void stop(int delay) {
		this.server.stop(delay);
	}
	
	public void stop() {
		this.stop(0);
	}
	
	public void addHostHandler(String host, HostHandler hostHandler) {
		this.handler.addHostHandler(host, hostHandler);
	}
	
	public void removeHostHandler(String host) {
		this.handler.removeHostHandler(host);
	}
	
	@Override
	public void run() {
		this.start();
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		 Proxy proxy = new Proxy(8000);
		 proxy.run();
	}
}
