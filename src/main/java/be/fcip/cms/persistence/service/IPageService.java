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

    PageableResult<PageEntity> findWebContent(String locale, String type, String contentType, Long pageNumber, Long limit);

    // Save
    @PreAuthorize("hasAnyRole('ROLE_ADMIN_CMS', 'ROLE_ADMIN_WEBCONTENT')")
    PageEntity saveContent(PageEntity p);
    @PreAuthorize("hasAnyRole('ROLE_ADMIN_CMS', 'ROLE_ADMIN_WEBCONTENT')")
    List<PageEntity> saveContent(List<PageEntity> pages);
    @PreAuthorize("hasAnyRole('ROLE_ADMIN_CMS', 'ROLE_ADMIN_WEBCONTENT')")
    PageContentEntity saveContentData(PageContentEntity content);

    // Delete
    @PreAuthorize("hasAnyRole('ROLE_ADMIN_CMS_DELETE', 'ROLE_ADMIN_WEBCONTENT_DELETE')")
    void deleteContent(Long id) throws Exception;
    @PreAuthorize("hasAnyRole('ROLE_ADMIN_CMS_DELETE', 'ROLE_ADMIN_WEBCONTENT_DELETE')")
    void deleteContentData(Long id) throws Exception;

    String getNav(Long contentId, String lang, long depth, Long currentContentId, boolean onlyTitle, Integer rootOffset, Integer limitRoot, Long websiteId);

    String getBreadcrumb(PageEntity content, String locale, String seperator, Long parendId, boolean h1);

    String getPagesTree(String lang, String type, Long websiteId);

    PageEntity findContent(Long id);

    PageEntity findPageEntity(Long id);

    PageContentEntity findPageContentEntity(Long id);

    boolean contentCanBeDeleted(PageEntity content, String contentDataLocale);

    boolean contentIsVisible(PageEntity content);

    boolean contentIsVisible(PageEntity content, PageContentEntity contentDataEntity);

    boolean contentIsPrivate(PageEntity content);

    String getContentJsonByTypeAndParams(String contentType, Map<String,String> params) throws Exception;

    PageContentEntity findContentData(Long id);

    PageEntity findBySlug(String slug, Locale locale);

    Collection<PermissionEntity> getRoleForContent(PageEntity content);

    PageableResult<PageEntity> findWebContent(String locale, Long year, String name, String type, String theme, String tags, String contentType, Long pageNumber, Long limit, Boolean isPrivate);

    PageableResult<PageEntity> findWebContent(String locale, Long yearStart, Long yearEnd, String name, String type, String theme, String tags, String contentType, Long pageNumber, Long limit, Boolean isPrivate);

    PageableResult<PageEntity> findWebContent(String locale, LocalDateTime begin, LocalDateTime end, String name, String type, String theme, String tags, String contentType, Long pageNumber, Long limit, Boolean isPrivate);

    List<PageEntity> findParents(PageEntity parent);

    List<Number> getRevisionNumberList(Long id);

    PageContentEntity getRevisionEntity(Number id);

    Object[] getRevision(Number id);

    String getWebContentRuleJson() throws Exception;

    WebContentRuleTemplateEntity addRule(String name, Long templateId);

    Long findRule(String name);

    void deleteRule(String id);

    Map<Long, Set<Pattern>> getDynamicUrl() throws PatternSyntaxException;

    void clearCache(Long id);
}
