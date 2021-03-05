package be.fcip.cms.hook;

import org.cache2k.extra.spring.SpringCache2kCacheManager;

public interface ICacheConfigHook {

    void customize(SpringCache2kCacheManager cache);
}
