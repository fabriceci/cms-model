package be.fcip.cms.persistence.service;

import be.fcip.cms.model.db.PageableResult;
import be.fcip.cms.persistence.model.PageContentEntity;
import be.fcip.cms.persistence.model.PageEntity;
import be.fcip.cms.persistence.model.PermissionEntity;
import be.fcip.cms.persistence.model.WebContentRuleTemplateEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public interface IPageService {

    PageableResult<PageEntity> search(String locale, String type, String contentType, Long pageNumber, Long limit);

    // Save
    @PreAuthorize("hasAnyRole('ROLE_ADMIN_CMS', 'ROLE_ADMIN_WEBCONTENT')")
    PageEntity savePage(PageEntity p);
    @PreAuthorize("hasAnyRole('ROLE_ADMIN_CMS', 'ROLE_ADMIN_WEBCONTENT')")
    List<PageEntity> savePage(List<PageEntity> pages);
    @PreAuthorize("hasAnyRole('ROLE_ADMIN_CMS', 'ROLE_ADMIN_WEBCONTENT')")
    PageContentEntity savePageData(PageContentEntity content);

    // Delete
    @PreAuthorize("hasAnyRole('ROLE_ADMIN_CMS_DELETE', 'ROLE_ADMIN_WEBCONTENT_DELETE')")
    void deleteContent(Long id) throws Exception;
    @PreAuthorize("hasAnyRole('ROLE_ADMIN_CMS_DELETE', 'ROLE_ADMIN_WEBCONTENT_DELETE')")
    void deleteContentData(Long id) throws Exception;

    String getNavCached(Long contentId, String lang, long depth, Long currentContentId, boolean onlyTitle, Integer rootOffset, Integer limitRoot, Long websiteId);

    String getBreadcrumbCached(PageEntity content, String locale, String seperator, Long parendId, boolean h1);

    String getPagesTreeCached(String lang, String type, Long websiteId);

    PageEntity findPage(Long id, boolean withJoin);

    PageEntity findPageCached(Long id);

    PageContentEntity findPageContent(Long id);

    boolean pageCanBeDeleted(PageEntity content, String contentDataLocale);

    boolean pageIsVisible(PageEntity content);

    boolean pageIsVisible(PageEntity content, PageContentEntity contentDataEntity);

    boolean pageIsPrivate(PageEntity content);

    String getContentJsonByTypeAndParams(String contentType, Map<String,String> params) throws Exception;

    PageEntity findBySlugCached(String slug, Locale locale);

    Collection<PermissionEntity> getRoleForPage(PageEntity content);

    PageableResult<PageEntity> search(String locale, Long year, String name, String type, String theme, String tags, String contentType, Long pageNumber, Long limit, Boolean isPrivate);

    PageableResult<PageEntity> search(String locale, Long yearStart, Long yearEnd, String name, String type, String theme, String tags, String contentType, Long pageNumber, Long limit, Boolean isPrivate);

    PageableResult<PageEntity> search(String locale, LocalDateTime begin, LocalDateTime end, String name, String type, String theme, String tags, String contentType, Long pageNumber, Long limit, Boolean isPrivate);

    List<PageEntity> findParents(PageEntity parent);

    String getWebContentRuleJson() throws Exception;

    WebContentRuleTemplateEntity addRule(String name, Long templateId);

    Long findRule(String name);

    void deleteRule(String id);

    Map<Long, Set<Pattern>> getDynamicUrlCached() throws PatternSyntaxException;

    void clearCache(Long id);
}
