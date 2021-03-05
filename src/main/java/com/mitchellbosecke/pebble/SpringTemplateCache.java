package com.mitchellbosecke.pebble;

import com.mitchellbosecke.pebble.cache.PebbleCache;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.springframework.cache.Cache;

import java.util.function.Function;

public class SpringTemplateCache implements PebbleCache<Object, PebbleTemplate> {

    private final Cache templateCache;

    public SpringTemplateCache(Cache tagCache) {
        this.templateCache = tagCache;
    }

    @Override
    public PebbleTemplate computeIfAbsent(Object key,
                                          Function<? super Object, ? extends PebbleTemplate> mappingFunction) {
        Cache.ValueWrapper valueWrapper = this.templateCache.get(key);
        if(valueWrapper != null && valueWrapper.get() != null){
            return (PebbleTemplate)valueWrapper.get();
        }
        PebbleTemplate apply = mappingFunction.apply(key);
        templateCache.put(key, apply);
        return apply;
    }

    @Override
    public void invalidateAll() {
        this.templateCache.clear();
    }
}
