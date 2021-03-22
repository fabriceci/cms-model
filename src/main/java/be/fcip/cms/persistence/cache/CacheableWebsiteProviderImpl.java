package be.fcip.cms.persistence.cache;

import be.fcip.cms.persistence.model.WebsiteEntity;
import be.fcip.cms.persistence.repository.IWebsiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CacheableWebsiteProviderImpl implements ICacheableWebsiteProvider {

    @Autowired IWebsiteRepository repository;

    @Override
    @Cacheable(value = "global", key = "'allWebsite'")
    public Map<Long, WebsiteEntity> findAllCached() {
        return Collections.unmodifiableMap(repository.findAll().stream().collect(Collectors.toMap(WebsiteEntity::getId, w -> w)));
    }
}
