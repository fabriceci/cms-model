package be.fcip.cms.persistence.cache;

import be.fcip.cms.model.db.PageableResult;
import be.fcip.cms.persistence.model.PageEntity;
import org.springframework.cache.annotation.Cacheable;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public interface ICacheablePageProvider {

    Long findContentId(String slug, Locale locale);

    PageEntity findContent(Long id);

    PageableResult<PageEntity> findWebContent(String locale, LocalDateTime begin, LocalDateTime end, String name, String type, String theme, String tags, String contentType, Long pageNumber, Long limit, Boolean isPrivate);

    @Cacheable(value = "dynamicSlug")
    Map<Long, Set<Pattern>> getDynamicUrl() throws PatternSyntaxException;
}
