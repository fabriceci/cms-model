package be.fcip.cms.persistence.repository;

import be.fcip.cms.model.db.PageableResult;
import be.fcip.cms.persistence.model.PageEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface IPageRepositoryCustom {
    PageEntity findContentCustom(Long id);

    PageableResult<PageEntity> findWebContentCustom(String locale, LocalDateTime begin, LocalDateTime end, String name, String type, String theme, String tags, String contentType, Long pageNumber, Long limit, Boolean isPrivate);

    List<PageEntity> findRootsByContentIdCustom(Long contentId, String locale, boolean onlyMenuItem);
}
