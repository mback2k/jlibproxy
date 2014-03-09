package de.uxnr.proxy.util;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class ListMap<K, V> extends LinkedHashMap<K, List<V>> {
  private static final long serialVersionUID = -3822822571164080258L;

  @Override
  public boolean containsValue(Object value) {
    return this.values().contains(value);
  }

  public V getFirst(Object key) {
    if (this.containsKey(key)) {
      return this.get(key).get(0);
    }
    return null;
  }

  public V add(K key, V value) {
    List<V> list;
    if (this.containsKey(key)) {
      list = this.get(key);
      list.add(value);
    } else {
      list = new LinkedList<V>();
      list.add(value);
      this.put(key, list);
    }
    return value;
  }

  public void addAll(Map<? extends K, ? extends V> map) {
    for (Entry<? extends K, ? extends V> entry : map.entrySet()) {
      this.add(entry.getKey(), entry.getValue());
    }
  }

  public List<V> remove(Object key, Object value) {
    List<V> list = null;
    if (this.containsKey(key)) {
      list = this.get(key);
      list.remove(value);
      if (list.isEmpty()) {
        this.remove(key);
      }
    }
    return list;
  }

  public Set<V> valueSet() {
    Set<V> values = new HashSet<V>();
    for (List<V> value : this.values()) {
      values.addAll(value);
    }
    return values;
  }
}
