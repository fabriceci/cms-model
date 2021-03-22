package com.mitchellbosecke.pebble;

import com.mitchellbosecke.pebble.cache.CacheKey;
import com.mitchellbosecke.pebble.cache.PebbleCache;
import org.cache2k.Cache;
import org.cache2k.Cache2kBuilder;

import java.util.function.Function;

public class Cache2kTagCache implements PebbleCache<CacheKey, Object> {

    private final Cache<CacheKey, Object> tagCache;

    public Cache2kTagCache() {
        tagCache = Cache2kBuilder.of(CacheKey.class, Object.class).entryCapacity(200).build();
    }

    public Cache2kTagCache(Cache<CacheKey, Object> tagCache) {
        this.tagCache = tagCache;
    }

    @Override
    public Object computeIfAbsent(CacheKey key, Function<? super CacheKey, ?> mappingFunction) {

        return this.tagCache.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public void invalidateAll() {
        this.tagCache.clear();
    }
}
