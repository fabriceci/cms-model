package be.fcip.cms.persistence.cache;

import be.fcip.cms.persistence.model.PageContentEntity;
import be.fcip.cms.persistence.model.PageEntity;
import be.fcip.cms.persistence.model.PermissionEntity;

import java.util.Collection;

public interface ICacheablePageUtilProvider {
    Collection<PermissionEntity> getRoles(PageEntity content);

    boolean contentIsPrivate(PageEntity content);

    boolean contentIsVisible(PageEntity content);

    boolean contentIsVisible(PageEntity content, PageContentEntity contentData);

    boolean contentCanBeDeleted(PageEntity content, String contentDataLocale);
}
