package com.len.messaging.util;

import java.util.HashMap;
import java.util.Map;

public class MapBuilder<K,V> {

    private Map<K, V> map = new HashMap<>();

    public static <K,V> MapBuilder<K, V> create(Class<K> typeKey, Class<V> typeValue) {
        return new MapBuilder<>();
    }

    public static <V> MapBuilder<String, V> create(Class<V> typeValue) {
        return create(String.class, typeValue);
    }

    public static MapBuilder<String, String> create() {
        return create(String.class);
    }

    public MapBuilder<K, V> put(K key, V value) {
        if (key != null) {
            map.put(key, value);
        }
        return this;
    }

    public MapBuilder<K, V> putAll(Map<K,V> addMap) {
        if (map != null) {
            map.putAll(addMap);
        }
        return this;
    }

    public Map<K,V> toMap() {
        return map;
    }

}
