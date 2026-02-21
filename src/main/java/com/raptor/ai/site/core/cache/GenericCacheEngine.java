package com.raptor.ai.site.core.cache;


import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class GenericCacheEngine<K,V> {

    private static final GenericCacheEngine INSTANCE = new GenericCacheEngine<>();
    @Inject
    Logger logger;

    private final ConcurrentMap<K,V> CACHE = new ConcurrentHashMap<>(30);
    private GenericCacheEngine() {
        super();
    }

    public static GenericCacheEngine getInstance() {
        return INSTANCE;
    }

    public V get(final String key) {
        return CACHE.get(key);
    }

    public void addEntry(final K key, final V value) {
        CACHE.put(key, value);
        return;
    }

    public void clearAll() {
        this.CACHE.clear();

        return;
    }

}
