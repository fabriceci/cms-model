package com.mitchellbosecke.pebble;

import com.mitchellbosecke.pebble.cache.CacheKey;
import com.mitchellbosecke.pebble.cache.PebbleCache;
import org.springframework.cache.Cache;

import java.util.function.Function;

public class SpringTagCache implements PebbleCache<CacheKey, Object> {

    private final Cache tagCache;

    public SpringTagCache(Cache tagCache) {
        this.tagCache = tagCache;
    }

    @Override
    public Object computeIfAbsent(CacheKey key, Function<? super CacheKey, ?> mappingFunction) {
        Cache.ValueWrapper valueWrapper = this.tagCache.get(key);
        if(valueWrapper != null && valueWrapper.get() != null){
            return valueWrapper.get();
        }
        Object apply = mappingFunction.apply(key);
        tagCache.put(key, apply);
        return apply;
        /*
        Cache.ValueWrapper valueWrapper = this.tagCache.get(key);
        int i = key.hashCode();
        return tagCache.putIfAbsent(key, mappingFunction.apply(key));

         */
    }

    @Override
    public void invalidateAll() {
        this.tagCache.clear();
    }
}
