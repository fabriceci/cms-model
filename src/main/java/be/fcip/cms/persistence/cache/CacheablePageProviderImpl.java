package be.fcip.cms.persistence.cache;

import be.fcip.cms.model.db.PageableResult;
import be.fcip.cms.persistence.model.PageContentEntity;
import be.fcip.cms.persistence.model.PageEntity;
import be.fcip.cms.persistence.repository.IPageContentRepository;
import be.fcip.cms.persistence.repository.IPageRepository;
import be.fcip.cms.util.ApplicationUtils;
import be.fcip.cms.util.CmsContentUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Service
public class CacheablePageProviderImpl implements ICacheablePageProvider {

    @Autowired
    private IPageRepository contentRepository;
    @Autowired
    private IPageContentRepository contentDataRepository;
 
    @PersistenceContext(unitName = "core")
    private EntityManager entityManager;

    @Cacheable(value = "page", key = "#slug + '_' + #locale.toString()")
    @Override
    public Long findContentId(String slug, Locale locale) {
        return contentDataRepository.findContentIdByComputedSlugAndLanguageLocale(slug, locale.toString());
    }

    @Cacheable(value = "page", key = "#id")
    @Override
    public PageEntity findContent(Long id) {
        return contentRepository.findContentCustom(id);
    }

    @Override
    @Cacheable(value = "pageGlobal")
    public PageableResult<PageEntity> findWebContent(String locale, LocalDateTime begin, LocalDateTime end, String name, String type, String theme, String tags, String contentType, Long pageNumber, Long limit, Boolean isPrivate) {

        PageableResult<PageEntity> pageablePageEntity = new PageableResult<>();

        PageableResult<PageEntity> queryResult = contentRepository.findWebContentCustom(locale, begin, end, name, type, theme, tags, contentType, pageNumber, limit, isPrivate);

        pageablePageEntity.setResult(queryResult.getResult());
        pageablePageEntity.setCurrentPage(queryResult.getCurrentPage());
        pageablePageEntity.setTotalPage(queryResult.getTotalPage());
        pageablePageEntity.setTotalResult(queryResult.getTotalResult());

        return pageablePageEntity;
    }

    @Override
    @Cacheable(value = "pageGlobal")
    public Map<Long, Set<Pattern>> getDynamicUrl() throws PatternSyntaxException {
        Map<Long, Set<Pattern>> map = new HashMap<>();
        Set<PageEntity> allDynamicUrlPages = contentRepository.findAllDynamicUrlPages();
        for (PageEntity page : allDynamicUrlPages) {
            for (Map.Entry<String, PageContentEntity> entry : page.getContentMap().entrySet()) {
                Set<Pattern> patterns = CmsContentUtils.computeDynamicUrl(entry.getValue().getSlug(), ApplicationUtils.forceLangInUrl, entry.getKey());
                map.put(page.getId(), patterns);
            }
        }
        return map;
    }
}
