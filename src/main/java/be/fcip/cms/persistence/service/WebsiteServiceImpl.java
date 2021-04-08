package be.fcip.cms.persistence.service;

import be.fcip.cms.persistence.cache.ICacheableWebsiteProvider;
import be.fcip.cms.persistence.model.PageEntity;
import be.fcip.cms.persistence.model.WebsiteEntity;
import be.fcip.cms.persistence.repository.IAuditRepository;
import be.fcip.cms.persistence.repository.IWebsiteRepository;
import be.fcip.cms.util.ApplicationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service(value = "websiteService")
@Transactional
@Slf4j
public class WebsiteServiceImpl implements IWebsiteService{

    @Autowired IWebsiteRepository websiteRepository;
    @Autowired ICacheableWebsiteProvider cacheableProvider;
    @Autowired IAuditRepository auditRepository;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "pebble", key = "'website_err_' + #website.id"),
            @CacheEvict(value = "pebble", key = "'website_tpl_' + #website.id"),
            @CacheEvict(value = "global", key = "'allWebsite'"),
            @CacheEvict(value= "pageFull", allEntries = true),
            @CacheEvict(value = "pageShort", allEntries = true)
    })
    public WebsiteEntity save(WebsiteEntity website) {
        if(website.getId() == 0){
            website.addTranslatableProperty("seo_description", ApplicationUtils.defaultLocale.toString(), "");
            website.addTranslatableProperty("seo_tags", ApplicationUtils.defaultLocale.toString(), "");
            website.addTranslatableProperty("seo_title", ApplicationUtils.defaultLocale.toString(), "");
            website.addTranslatableProperty("seo_h1", ApplicationUtils.defaultLocale.toString(), "");
            website.addTranslatableProperty("sitename", ApplicationUtils.defaultLocale.toString(), "");
        }
        return websiteRepository.save(website);
    }

    @Override
    public Map<Long, WebsiteEntity> findAllCached() {
        return  cacheableProvider.findAllCached();
    }

    @Override
    public Optional<WebsiteEntity> findById(Long id) {
        return websiteRepository.findById(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "pebble", key = "'website_err_' + #id"),
            @CacheEvict(value = "pebble", key = "'website_tpl_' + #id"),
            @CacheEvict(value = "global", key = "'allWebsite'"),
            @CacheEvict(value= "pageFull", allEntries = true),
            @CacheEvict(value = "pageShort", allEntries = true)
    })
    public void delete(Long id) {
        websiteRepository.deleteById(id);
    }

    @Override
    public WebsiteEntity getWebsiteFromPage(PageEntity page) {
        if(page == null) throw new RuntimeException("Page can't be null");
        return findAllCached().get(page.getWebsite().getId());
    }

    public WebsiteEntity getWebsiteFromUrl(HttpServletRequest request){
        return getWebsiteFromUrl(request.getRequestURI());
    }

    public WebsiteEntity getWebsiteFromUrl(String path){
        int cpt = 0;
        Map<Long, WebsiteEntity> all = findAllCached();
        for (WebsiteEntity website : all.values()) {
            if (cpt == 0) {
                cpt++; // skip first
                continue;
            }
            if(ApplicationUtils.forceLangInUrl){
                for (Locale siteLocale : ApplicationUtils.locales) {
                    if (path.startsWith("/" + siteLocale + website.getSlug())) {
                        return website;
                    }
                }

            } else {
                if (path.startsWith(website.getSlug())) {
                    return website;
                }
            }

        }
        return all.get(1L);
    }
}
