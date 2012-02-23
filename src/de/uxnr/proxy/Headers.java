package de.uxnr.proxy;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("restriction")
public class Headers implements Map<String, List<String>> {
	private final com.sun.net.httpserver.Headers headers;

	public Headers() {
		this.headers = new com.sun.net.httpserver.Headers();
	}

	protected Headers(com.sun.net.httpserver.Headers headers) {
		this.headers = headers;
	}

	public void add(String key, String value) {
		this.headers.add(key, value);
	}

	public String getFirst(String key) {
		return this.headers.getFirst(key);
	}
	
	public void set(String key, String value) {
		this.headers.set(key, value);
	}

	@Override
	public void clear() {
		this.headers.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return this.headers.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return this.headers.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<String, List<String>>> entrySet() {
		return this.headers.entrySet();
	}

	@Override
	public List<String> get(Object key) {
		return this.headers.get(key);
	}

	@Override
	public boolean isEmpty() {
		return this.headers.isEmpty();
	}

	@Override
	public Set<String> keySet() {
		return this.headers.keySet();
	}

	@Override
	public List<String> put(String key, List<String> value) {
		return this.headers.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends List<String>> m) {
		this.headers.putAll(m);
	}

	@Override
	public List<String> remove(Object key) {
		return this.headers.remove(key);
	}

	@Override
	public int size() {
		return this.headers.size();
	}

	@Override
	public Collection<List<String>> values() {
		return this.headers.values();
	}
}
