package com.mitchellbosecke.pebble;

import com.mitchellbosecke.pebble.cache.CacheKey;
import com.mitchellbosecke.pebble.cache.PebbleCache;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

public class ConcurrentMapTagCacheCusto implements PebbleCache<CacheKey, Object> {

    private final ConcurrentMap<CacheKey, Object> tagCache;

    public ConcurrentMapTagCacheCusto() {
        this.tagCache = new ConcurrentHashMap<>(200);
    }

    public ConcurrentMapTagCacheCusto(ConcurrentMap<CacheKey, Object> tagCache) {
        this.tagCache = tagCache;
    }

    @Override
    public Object computeIfAbsent(CacheKey key,
                                  Function<? super CacheKey, ?> mappingFunction) {
        Object o = tagCache.get(key);
        Set<Map.Entry<CacheKey, Object>> entries = this.tagCache.entrySet();
        int i = key.hashCode();
        tagCache.put(key, mappingFunction.apply(key));
        return this.tagCache.computeIfAbsent(key, mappingFunction);

       // return this.tagCache.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public void invalidateAll() {
        this.tagCache.clear();
    }
}
