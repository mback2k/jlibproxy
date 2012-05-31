package de.uxnr.proxy;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.sun.net.httpserver.HttpServer;

public class Proxy implements Runnable {
	private final ProxyHandler handler;
	private final InetSocketAddress address;
	private final BlockingQueue<Runnable> queue;
	private final Executor executor;
	private final HttpServer server;

	private final int backlog = 10;
	private final int poolSize = 2;
	private final int maxPoolSize = 512;
	private final int keepAliveTime = 10;

	public Proxy(InetSocketAddress address) throws IOException {
		this.address = address;
		this.handler = new ProxyHandler();
		this.queue = new SynchronousQueue<Runnable>();
		this.executor = new ThreadPoolExecutor(this.poolSize, this.maxPoolSize, this.keepAliveTime, TimeUnit.SECONDS, this.queue);
		this.server = HttpServer.create(this.address, this.backlog);
		this.server.createContext("/", this.handler);
		this.server.setExecutor(this.executor);
	}

	public Proxy(int port) throws IOException {
		this(new InetSocketAddress("localhost", port));
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

	public void addHostRewriter(String host, HostRewriter hostRewriter) {
		this.handler.addHostRewriter(host, hostRewriter);
	}

	public void removeHostRewriter(String host) {
		this.handler.removeHostRewriter(host);
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
