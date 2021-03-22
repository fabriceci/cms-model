package be.fcip.cms.service;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class PebbleServiceCacheProviderImpl implements IPebbleServiceCacheProvider{

    @Autowired @Qualifier("pebbleStringEngine") private PebbleEngine pebbleStringEngine;

    @Override @Cacheable(value = "pebble", key = "#cacheKey")
    public PebbleTemplate getCompiledTemplate(String content, String cacheKey) {
        return pebbleStringEngine.getTemplate(content);
    }
}
