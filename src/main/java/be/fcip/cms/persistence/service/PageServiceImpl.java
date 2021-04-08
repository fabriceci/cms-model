package be.fcip.cms.persistence.service;

import be.fcip.cms.exception.ResourceNotFoundException;
import be.fcip.cms.model.MenuItem;
import be.fcip.cms.model.db.PageableResult;
import be.fcip.cms.persistence.cache.ICacheablePageProvider;
import be.fcip.cms.persistence.cache.ICacheablePageTreeProvider;
import be.fcip.cms.persistence.model.*;
import be.fcip.cms.persistence.repository.*;
import be.fcip.cms.util.ApplicationUtils;
import be.fcip.cms.util.CmsDateUtils;
import be.fcip.cms.util.CmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    @Autowired private IPageRepository pageRepository;
    @Autowired private IPageContentRepository pageContentRepository;
    @Autowired private IWebContentRuleTemplateRepository webContentRuleTemplateRepository;
    @Autowired private IPageTemplateRepository templateRepository;
    @Autowired private CacheManager cacheManager;

    // cacheable service
    @Autowired private ICacheablePageProvider cacheablePageProvider;
    @Autowired private ICacheablePageTreeProvider cacheablePageTreeProvider;

    /**
     * Public page / when user request a page
     */
    @Override
    public PageEntity findBySlugCached(String slug, Locale locale) {

        Long pageId = cacheablePageProvider.findContentId(slug, locale);
        if (pageId == null)
            return null;

        return cacheablePageProvider.findContent(pageId);
    }


    @Override
    public PageContentEntity findPageContent(Long id) {
        return pageContentRepository.findById(id).orElse(null);
    }

    @Override
    public PageEntity findPage(Long id, boolean withJoin) {
        return withJoin ? pageRepository.findContentCustom(id) : pageRepository.findOne(id);
    }

    @Override
    public PageEntity findPageCached(Long id) {
        return cacheablePageProvider.findContent(id);
    }

    @Override
    public List<PageEntity> findParents(PageEntity parent) {
        return pageRepository.findByPageParentIdOrderByPositionAsc(parent.getId());
    }

    @Override
    public PageableResult<PageEntity> search(String locale, Long year, String name, String type, String theme, String tags, String contentType, Long pageNumber, Long limit, Boolean isPrivate) {
        LocalDateTime begin = null;
        LocalDateTime end = null;
        if (year != null && year != 0) {
            begin = CmsDateUtils.getBeginDateYear(year.intValue());
            end = CmsDateUtils.getEndDateYear(year.intValue());
        }

        return cacheablePageProvider.findWebContent(locale, begin, end, name, type, theme, tags, contentType, pageNumber, limit, isPrivate);
    }

    @Override
    public PageableResult<PageEntity> search(String locale, Long yearStart, Long yearEnd, String name, String type, String theme, String tags, String contentType, Long pageNumber, Long limit, Boolean isPrivate) {
        LocalDateTime begin = null;
        LocalDateTime end = null;
        if (yearStart != null && yearStart != 0) {
            begin = CmsDateUtils.getBeginDateYear(yearStart.intValue());
            end = CmsDateUtils.getEndDateYear(yearEnd.intValue());
        }

        return cacheablePageProvider.findWebContent(locale, begin, end, name, type, theme, tags, contentType, pageNumber, limit, isPrivate);
    }


    @Override
    public PageableResult<PageEntity> search(String locale, LocalDateTime begin, LocalDateTime end, String name, String type, String theme, String tags, String contentType, Long pageNumber, Long limit, Boolean isPrivate) {

        return cacheablePageProvider.findWebContent(locale, begin, end, name, type, theme, tags, contentType, pageNumber, limit, isPrivate);
    }


    @Override
    public PageableResult<PageEntity> search(String locale, String type, String contentType, Long pageNumber, Long limit) {

        return cacheablePageProvider.findWebContent(locale, null, null, null, type, null, null, contentType, pageNumber, limit, null);
    }


    @Override
    @Caching(evict = {
            @CacheEvict(value = "global", key= "'dynamicSlug'"),
            @CacheEvict(value = "pageGlobal", allEntries = true),
    })
    public void removeFromCache(Long id) {
        clearCache(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "global", key= "'dynamicSlug'"),
            @CacheEvict(value = "pageGlobal", allEntries = true),
    })
    public PageEntity savePage(PageEntity p) {

        clearCache(p.getId());
        if(p.getPageParent() != null){
            clearCache(p.getPageParent().getId());
        }
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
    public List<PageEntity> savePage(List<PageEntity> pages) {
        for (PageEntity page : pages) {
            page = savePage(page);
        }
        return pages;
    }

    public void clearCache(Long id) {
        if(id == 0) return;
        PageEntity content = cacheablePageProvider.findContent(id);
        // Clear Page full
        Cache fullCache = cacheManager.getCache("pageFull");
        Cache shortCache = cacheManager.getCache("pageShort");
        // full clear because the page can be in a menu
        fullCache.clear();
        shortCache.clear();

        // Clear PageBySlug
        Cache pageCache = cacheManager.getCache("page");
        pageCache.evict(id);
        pageCache.evict(id + "_top");
        pageCache.evict(id + "_bot");
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
        if(current.getPageParent() != null){
            clearCache(current.getPageParent().getId());
        }
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
    public PageContentEntity savePageData(PageContentEntity content) {
        clearCache(content.getPage().getId());
        return pageContentRepository.save(content);
    }

    @Override
    public String getNavCached(Long pageId, String lang, long depth, Long currentContentId, boolean onlyTitle, Integer rootOffset, Integer limitRoot, Long websiteId, String ulChildrenCLass, String liChildrenClass, String linkClass) {
        return cacheablePageTreeProvider.getMenu(pageId, lang, depth, currentContentId, onlyTitle, rootOffset, limitRoot, websiteId,  ulChildrenCLass, liChildrenClass, linkClass);
    }

    @Override
    public List<MenuItem> getNavItemCached(Long contentId, String lang, long depth, Long currentContentId, boolean onlyTitle, Integer rootOffset, Integer limitRoot, Long websiteId) {
        return cacheablePageTreeProvider.getMenuItem(contentId, lang, depth, currentContentId, onlyTitle, rootOffset, limitRoot, websiteId);
    }

    public String getBreadcrumb(PageEntity pageEntity, String locale, String separator) {
        return getBreadcrumbCached(pageEntity, locale, separator, null, false);
    }

    @Override
    public String getBreadcrumbCached(PageEntity content, String locale, String separator, Long parendId, boolean h1) {
        return cacheablePageTreeProvider.getBreadcrumb(content, locale, separator, parendId, h1);
    }

    @Override
    public String getContentJsonByTypeAndParams(String pageType, Map<String, String> params) throws Exception {

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

        PageableResult<PageEntity> result = this.search(lang, year, null, types, theme, tag, pageType, 0L, 0L, isPrivate);

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
    public String getPagesTreeCached(String lang, String type, Long websiteId) {
        return cacheablePageTreeProvider.getPagesTree(lang, type, websiteId);
    }

    @Override
    public boolean pageCanBeDeleted(PageEntity content, String contentDataLocale) {
        if(StringUtils.isEmpty(contentDataLocale)){
            throw new IllegalArgumentException("contentDataLocale can't be null or empty");
        }

        if (content == null || content.getId() == 0) return false;

        for (PageEntity c : content.getPageChildren()) {
            PageEntity children = cacheablePageProvider.findContent(c.getId());
            if(children == null) {
                log.error("Page with id " + content.getId() + " has children that doesn't exist (cache issue)");
                return true;
            }
            PageContentEntity contentDataDto = children.getContentMap().get(contentDataLocale);

            if (contentDataDto  == null ) {
                return false;
            }

        }
        return true;
    }

    @Override
    public boolean pageIsPrivate(PageEntity content) {
        // force to call cache
        PageEntity parent = null;

        if (content == null || content.getId() == 0) return false;

        if (content.isMemberOnly())
            return true;

        long parentId = content.getPageParent() != null ? content.getPageParent().getId() : 0;

        while (true) {
            // no more parent
            if (parentId == 0) {
                return false;
            }
            parent = cacheablePageProvider.findContent(parentId);
            if (parent.isMemberOnly()) {
                return true;
            }
            parentId = parent.getPageParent() != null ? parent.getPageParent().getId() : 0;
        }
    }

    @Override
    public boolean pageIsVisible(PageEntity content) {
        // force to call cache
        PageEntity parent = null;

        if (content == null || content.getId() == 0 || !content.isEnabled())
            return false;

        long parentId = content.getPageParent() != null ? content.getPageParent().getId() : 0;
        while (true) {
            // no more parent
            if (parentId == 0) {
                return true;
            }
            parent = cacheablePageProvider.findContent(parentId);

            if (!parent.isEnabled()) {
                return false;
            }
            parentId = parent.getPageParent() != null ? parent.getPageParent().getId() : 0;
        }
    }

    @Override
    public boolean pageIsVisible(PageEntity content, PageContentEntity contentData) {
        // force to call cache
        PageEntity parent = null;
        PageContentEntity data = null;

        if (content == null || content.getId() == 0 || !content.isEnabled() || contentData == null || !contentData.isEnabled())
            return false;

        long parentId = content.getPageParent() != null ? content.getPageParent().getId() : 0;
        while (true) {
            // no more parent
            if (parentId == 0) {
                return true;
            }
            parent = cacheablePageProvider.findContent(parentId);
            data = parent.getContentMap().get(contentData.getLanguage());

            if (!parent.isEnabled()) {
                return false;
            }

            if (data != null && !data.isEnabled()) {
                return false;
            }
            parentId = parent.getPageParent() != null ? parent.getPageParent().getId() : 0;
        }
    }

    @Override
    public Collection<PermissionEntity> getRoleForPage(PageEntity content) {
        Collection<PermissionEntity> result = new HashSet<>();

        if (content == null || content.getId() == 0) return null;

        result.addAll(content.getPermissions());

        PageEntity parent = null;
        long parentId = content.getPageParent() != null ? content.getPageParent().getId() : 0;
        while (true) {
            // no more parent
            if (parentId == 0) {
                break;
            }
            parent = cacheablePageProvider.findContent(parentId);

            result.addAll(parent.getPermissions());

            parentId = parent.getPageParent() != null ? parent.getPageParent().getId() : 0;
        }
        return result;
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
    public Map<Long, Set<Pattern>> getDynamicUrlCached() throws PatternParseException {
        return cacheablePageProvider.getDynamicUrl();
    }
}
