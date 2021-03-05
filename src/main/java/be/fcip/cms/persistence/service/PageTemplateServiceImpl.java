package be.fcip.cms.persistence.service;

import be.fcip.cms.persistence.cache.ICacheableTemplateProvider;
import be.fcip.cms.persistence.model.PageEntity;
import be.fcip.cms.persistence.model.PageTemplateEntity;
import be.fcip.cms.persistence.repository.IPageRepository;
import be.fcip.cms.persistence.repository.IPageTemplateRepository;
import be.fcip.cms.util.ApplicationUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
@Transactional
@Slf4j
public class PageTemplateServiceImpl implements IPageTemplateService {

    @Autowired
    private IPageTemplateRepository contentTemplateRepository;

    @Autowired
    private ICacheableTemplateProvider cacheableTemplateProvider;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private IPageRepository pageRepository;

    @Override
    public List<PageTemplateEntity> findAllByTypeLike(String type) {
        return contentTemplateRepository.findByTypeLike(type);
    }

    @Override
    public PageTemplateEntity find(Long id) {
        return cacheableTemplateProvider.find(id);
    }

    @Override
    public PageTemplateEntity findEntity(Long id) {
        return contentTemplateRepository.findById(id).orElse(null);
    }

    @Override
    public PageTemplateEntity findByName(String name) {
        return contentTemplateRepository.findFirstByName(name);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "template", key = "#contentTemplate.id"),
    })
    public PageTemplateEntity save(PageTemplateEntity contentTemplate) {
        cleanBlockCache(contentTemplate);
        return contentTemplateRepository.save(contentTemplate);
    }


    @Override
    @Caching(evict = {
            @CacheEvict(value = "template", key = "#id"),
    })
    public void delete(Long id) throws Exception {
        PageTemplateEntity contentTemplateEntity = contentTemplateRepository.findById(id).orElse(null);
        if (contentTemplateEntity == null) {
            throw new Exception("Content Template with id " + id + " is not found!");
        } else if (!contentTemplateEntity.isDeletable()) {
            String message = "Content Template with name \" + name + \" is not deletable!";
            log.error(message);
            throw new Exception(message);
        }
        cleanBlockCache(contentTemplateEntity);
        contentTemplateRepository.deleteById(id);
    }

    private void cleanBlockCache(PageTemplateEntity pageTemplate){
        if(pageTemplate.getBlock() != null && pageTemplate.getBlock().getId() != 0){
            Cache cacheBlock = cacheManager.getCache("block");
            cacheBlock.evict(pageTemplate.getBlock().getId());
            cacheBlock.evict("compiled_block_" + pageTemplate.getBlock().getId());
        }
        Set<PageEntity> pages = pageRepository.findAllPagesByTemplate(pageTemplate.getId());
        Cache fullCache = cacheManager.getCache("pageFull");
        Cache shortCache = cacheManager.getCache("pageShort");
        for (PageEntity page : pages) {
            for (Locale locale : ApplicationUtils.locales) {
                String key = page.getId() + "_" + locale.toString();
                fullCache.evict(key);
                shortCache.evict(key);
            }
        }
    }

    @Override
    public String jsonContent() {

        List<PageTemplateEntity> contentTemplateEntityList = contentTemplateRepository.findAll();
        JsonArrayBuilder data = Json.createArrayBuilder();
        JsonObjectBuilder row;

        for (PageTemplateEntity c : contentTemplateEntityList) {
            row = Json.createObjectBuilder();
            row.add("DT_RowData", Json.createObjectBuilder().add("id", c.getId()));
            row.add("name", StringUtils.trimToEmpty(c.getName()));
            row.add("description", StringUtils.trimToEmpty(c.getDescription()));
            row.add("deletable", c.isDeletable());
            data.add(row);
        }

        return Json.createObjectBuilder().add("data", data).build().toString();
    }
}