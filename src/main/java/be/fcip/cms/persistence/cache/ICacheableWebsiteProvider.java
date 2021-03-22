package be.fcip.cms.persistence.cache;

import be.fcip.cms.persistence.model.WebsiteEntity;

import java.util.Map;

public interface ICacheableWebsiteProvider {
    Map<Long, WebsiteEntity> findAllCached();
}
