package be.fcip.cms.persistence.service;

import be.fcip.cms.exception.ResourceNotFoundException;
import be.fcip.cms.model.db.PageableResult;
import be.fcip.cms.persistence.cache.ICacheablePageProvider;
import be.fcip.cms.persistence.cache.ICacheablePageTreeProvider;
import be.fcip.cms.persistence.cache.ICacheablePageUtilProvider;
import be.fcip.cms.persistence.model.*;
import be.fcip.cms.persistence.repository.*;
import be.fcip.cms.util.ApplicationUtils;
import be.fcip.cms.util.CmsContentUtils;
import be.fcip.cms.util.CmsDateUtils;
import be.fcip.cms.util.CmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.pattern.PatternParseException;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;

@Service(value = "contentService")
@Transactional
@Slf4j
public class PageServiceImpl implements IPageService {

    // repository
    @Autowired
    private IAuditRepository auditRepository;
    @Autowired
    private IPageRepository pageRepository;
    @Autowired
    private IPageContentRepository pageContentRepository;
    @Autowired
    private IWebContentRuleTemplateRepository webContentRuleTemplateRepository;
    @Autowired
    private IPageTemplateRepository templateRepository;
    @Autowired
    private CacheManager cacheManager;

    // cacheable service
    @Autowired
    private ICacheablePageProvider cacheableContentProvider;
    @Autowired
    private ICacheablePageUtilProvider cacheableContentUtilProvider;
    @Autowired
    private ICacheablePageTreeProvider cacheableContentTreeProvider;

    /**
     * Public page / when user request a page
     */
    @Override
    public PageEntity findBySlug(String slug, Locale locale) {

        Long contentId = cacheableContentProvider.findContentId(slug, locale);
        if (contentId == null)
            return null;

        return cacheableContentProvider.findContent(contentId);
    }

    // Find Entity (Save/Update/Delete)
    @Override
    public PageEntity findPageEntity(Long id) {
        PageEntity one = pageRepository.findOne(id);
        Hibernate.initialize(one.getContentMap());
        return one;
    }

    @Override
    public PageContentEntity findPageContentEntity(Long id) {
        return pageContentRepository.findById(id).orElse(null);
    }

    // Find DTO
    @Override
    public PageContentEntity findContentData(Long id) {
        return pageContentRepository.findById(id).orElse(null);
    }

    @Override
    public PageEntity findContent(Long id) {
        return cacheableContentProvider.findContent(id);
    }

    @Override
    public List<PageEntity> findParents(PageEntity parent) {
        return pageRepository.findByPageParentIdOrderByPositionAsc(parent.getId());
    }

    @Override
    public PageableResult<PageEntity> findWebContent(String locale, Long year, String name, String type, String theme, String tags, String contentType, Long pageNumber, Long limit, Boolean isPrivate) {
        LocalDateTime begin = null;
        LocalDateTime end = null;
        if (year != null && year != 0) {
            begin = CmsDateUtils.getBeginDateYear(year.intValue());
            end = CmsDateUtils.getEndDateYear(year.intValue());
        }

        return cacheableContentProvider.findWebContent(locale, begin, end, name, type, theme, tags, contentType, pageNumber, limit, isPrivate);
    }

    @Override
    public PageableResult<PageEntity> findWebContent(String locale, Long yearStart, Long yearEnd, String name, String type, String theme, String tags, String contentType, Long pageNumber, Long limit, Boolean isPrivate) {
        LocalDateTime begin = null;
        LocalDateTime end = null;
        if (yearStart != null && yearStart != 0) {
            begin = CmsDateUtils.getBeginDateYear(yearStart.intValue());
            end = CmsDateUtils.getEndDateYear(yearEnd.intValue());
        }

        return cacheableContentProvider.findWebContent(locale, begin, end, name, type, theme, tags, contentType, pageNumber, limit, isPrivate);
    }


    @Override
    public PageableResult<PageEntity> findWebContent(String locale, LocalDateTime begin, LocalDateTime end, String name, String type, String theme, String tags, String contentType, Long pageNumber, Long limit, Boolean isPrivate) {

        return cacheableContentProvider.findWebContent(locale, begin, end, name, type, theme, tags, contentType, pageNumber, limit, isPrivate);
    }


    @Override
    public PageableResult<PageEntity> findWebContent(String locale, String type, String contentType, Long pageNumber, Long limit) {

        return cacheableContentProvider.findWebContent(locale, null, null, null, type, null, null, contentType, pageNumber, limit, null);
    }
    @Override
    @Caching(evict = {
            @CacheEvict(value = "global", key= "'dynamicSlug'"),
            @CacheEvict(value = "pageGlobal", allEntries = true),
    })
    public PageEntity saveContent(PageEntity p) {

        clearCache(p.getId());
        if (p.getId() == 0) {
            PageEntity parent = p.getPageParent();

            PageEntity result = pageRepository.findFirstByPageParentOrderByPositionDesc(parent);

            if (result == null) {
                p.setPosition(0);
            } else {
                p.setPosition(result.getPosition() + 1);
            }
        }

        return pageRepository.save(p);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "global", key= "'dynamicSlug'"),
            @CacheEvict(value = "pageGlobal", allEntries = true),
    })
    public List<PageEntity> saveContent(List<PageEntity> pages) {
        for (PageEntity page : pages) {
            page = saveContent(page);
        }
        return pages;
    }

    public void clearCache(Long id) {
        if(id == 0) return;
        PageEntity content = cacheableContentProvider.findContent(id);
        // Clear Page full
        Cache fullCache = cacheManager.getCache("pageFull");
        Cache shortCache = cacheManager.getCache("pageShort");
        // full clear because the page can be in a menu
        fullCache.clear();
        shortCache.clear();

        // Clear PageBySlug
        Cache pageCache = cacheManager.getCache("page");
        pageCache.evict(id);
        for (Map.Entry<String, PageContentEntity> entry : content.getContentMap().entrySet()) {
            PageContentEntity value = entry.getValue();
            pageCache.evict(value.getSlug() + "_" + entry.getKey());
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "global", key= "'dynamicSlug'"),
            @CacheEvict(value = "pageGlobal", allEntries = true),
    })
    public void deleteContent(Long id) throws Exception {
        PageEntity current = pageRepository.findOne(id);

        // Exist
        if (current == null)
            throw new ResourceNotFoundException();
        // No children
        if (current.getPageChildren().size() > 0)
            throw new Exception("Page with id = " + id + " has children !");
        // delete
        PageEntity parent = current.getPageParent();
        clearCache(current.getId());
        pageRepository.deleteById(id);

        List<PageEntity> pages = pageRepository.findByPageParentOrderByPositionAsc(parent);
        if (pages != null && pages.size() > 0) {
            int counter = 0;
            for (PageEntity p : pages) {
                p.setPosition(counter);
                counter++;
            }
            pageRepository.saveAll(pages);
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "global", key= "'dynamicSlug'"),
            @CacheEvict(value = "pageGlobal", allEntries = true)
    })
    public void deleteContentData(Long id) throws Exception {
        clearCache(id);
        pageContentRepository.deleteById(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "global", key= "'dynamicSlug'"),
            @CacheEvict(value = "pageGlobal", allEntries = true)
    })
    public PageContentEntity saveContentData(PageContentEntity content) {
        clearCache(content.getPage().getId());
        return pageContentRepository.save(content);
    }

    @Override
    public String getNav(Long contentId, String lang, long depth, Long currentContentId, boolean onlyTitle, Integer rootOffset, Integer limitRoot, Long websiteId) {
        return cacheableContentTreeProvider.getMenu(contentId, lang, depth, currentContentId, onlyTitle, rootOffset, limitRoot, websiteId);
    }

    public String getBreadcrumb(PageEntity content, String locale, String separator) {
        return getBreadcrumb(content, locale, separator, null, false);
    }

    @Override
    public String getBreadcrumb(PageEntity content, String locale, String separator, Long parendId, boolean h1) {
        return cacheableContentTreeProvider.getBreadcrumb(content, locale, separator, parendId, h1);
    }

    @Override
    public String getContentJsonByTypeAndParams(String contentType, Map<String, String> params) throws Exception {

        String lang = StringUtils.trimToNull(params.get("lang"));
        String theme = StringUtils.trimToNull(params.get("theme"));
        String tag = StringUtils.trimToNull(params.get("tag"));
        String types = StringUtils.trimToNull(params.get("type"));
        String contentPrivate = StringUtils.trimToNull(params.get("isPrivate"));
        String yearString = StringUtils.trimToNull(params.get("year"));

        boolean isPrivate = false;
        if (!StringUtils.isEmpty(contentPrivate) && contentPrivate.toLowerCase().equals("true")) {
            isPrivate = true;
        }
        if(!StringUtils.isEmpty(lang)) {
            lang = ApplicationUtils.getLocale(lang).toString();
        }
        Long year = null;
        if (!StringUtils.isEmpty(yearString)) {
            try {
                year = Long.parseLong(yearString);
            } catch (NumberFormatException e) {
                year = null;
            }
        }

        PageableResult<PageEntity> result = this.findWebContent(lang, year, null, types, theme, tag, contentType, 0L, 0L, isPrivate);

        JsonArrayBuilder data = Json.createArrayBuilder();
        JsonObjectBuilder row;
        // reload tree like this : table.ajax.reload()
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(CmsUtils.DATETIME_FORMAT);
        for (PageEntity c : result.getResult()) {
            PageContentEntity contentData = null;
            if (StringUtils.isEmpty(lang)) {
                contentData = c.getContentMap().get(ApplicationUtils.defaultLocale.toString());
                if (contentData == null) {
                    Map.Entry<String, PageContentEntity> entry = c.getContentMap().entrySet().iterator().next();
                    contentData = entry.getValue();
                }
            } else {
                contentData = c.getContentMap().get(lang);
            }
            StringBuilder s = new StringBuilder();
            for (String key : c.getContentMap().keySet()) {
                s.append(key).append(", ");
            }
            if (s.length() > 2) s = new StringBuilder(s.substring(0, s.length() - 2));
            String title = contentData.getTitle();
            if (StringUtils.isEmpty(title)) title = String.valueOf(c.getId());
            row = Json.createObjectBuilder();
            row.add("DT_RowData", Json.createObjectBuilder().add("id", c.getId())
                    .add("contentDataId", contentData.getId())
                    .add("lang", contentData.getLanguage()));
            // only content if all lang
            if (lang == null) {
                row.add("active", c.isEnabled());
            } else {
                row.add("active", c.isEnabled() && contentData.isEnabled());
            }
            row.add("title", StringUtils.trimToEmpty(title));

            row.add("lang", s.toString());
            row.add("dateBegin", formatDateTime(formatter, c.getBeginDate()));
            row.add("dateEnd", formatDateTime(formatter, c.getEndDate()));
            data.add(row);
        }

        return Json.createObjectBuilder().add("data", data).build().toString();
    }

    private static String formatDateTime(DateTimeFormatter dt, LocalDateTime date) {
        return (date == null ? "/" : StringUtils.trimToEmpty(dt.format(date)));
    }

    @Override
    public String getPagesTree(String lang, String type, Long websiteId) {
        return cacheableContentTreeProvider.getPagesTree(lang, type, websiteId);
    }

    @Override
    public boolean contentCanBeDeleted(PageEntity content, String contentDataLocale) {
        return cacheableContentUtilProvider.contentCanBeDeleted(content, contentDataLocale);
    }

    @Override
    public boolean contentIsPrivate(PageEntity content) {
        return cacheableContentUtilProvider.contentIsPrivate(content);
    }

    @Override
    public boolean contentIsVisible(PageEntity content) {
        return cacheableContentUtilProvider.contentIsVisible(content);
    }

    @Override
    public boolean contentIsVisible(PageEntity content, PageContentEntity contentData) {
        return cacheableContentUtilProvider.contentIsVisible(content, contentData);
    }

    @Override
    public Collection<PermissionEntity> getRoleForContent(PageEntity content) {
        return cacheableContentUtilProvider.getRoles(content);
    }

    @Override
    public List<Number> getRevisionNumberList(Long id) {

        return auditRepository.getRevisionNumberList(PageContentEntity.class, id);
    }

    @Override
    public PageContentEntity getRevisionEntity(Number id){
        try {
            return (PageContentEntity) auditRepository.getRevisionEntity(PageContentEntity.class, id);
        } catch(Exception e){
            log.error("Get Revision Entity Exception, ID : " + id, e);
            return null;
        }
    }

    @Override
    public Object[] getRevision(Number id) {
        return auditRepository.getRevision(PageContentEntity.class, id);
    }

    @Override
    public String getWebContentRuleJson() throws Exception {

        JsonArrayBuilder data = Json.createArrayBuilder();
        JsonObjectBuilder row = null;

        List<WebContentRuleTemplateEntity> all = webContentRuleTemplateRepository.findAll();


        for (WebContentRuleTemplateEntity w : all) {
            row = Json.createObjectBuilder();
            row.add("DT_RowData", Json.createObjectBuilder().add("id", w.getName()));
            row.add("name", w.getName());
            row.add("templateId", w.getTemplateId());
            PageTemplateEntity one = templateRepository.findById(w.getTemplateId()).orElse(null);
            if(one == null){
                row.add("templateName", "Not found");
            } else {
                row.add("templateName", one.getName());
            }
            data.add(row);
        }
        return Json.createObjectBuilder().add("data", data).build().toString();
    }

    @Override
    public WebContentRuleTemplateEntity addRule(String name, Long templateId) {

        WebContentRuleTemplateEntity one = webContentRuleTemplateRepository.findById(name).orElse(null);
        if(one == null){
            one = new WebContentRuleTemplateEntity();
            one.setName(name);
        }
        one.setTemplateId(templateId);

        return webContentRuleTemplateRepository.save(one);
    }

    @Override
    public void deleteRule(String id) {
        webContentRuleTemplateRepository.deleteById(id);
    }

    @Override
    public Long findRule(String name) {

        WebContentRuleTemplateEntity one = webContentRuleTemplateRepository.findById(name).orElse(null);
        return one == null ? null : one.getTemplateId();
    }

    @Override
    public Map<Long, Set<Pattern>> getDynamicUrl() throws PatternParseException {
        return cacheableContentProvider.getDynamicUrl();
    }
}
