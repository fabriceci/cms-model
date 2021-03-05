package be.fcip.cms.persistence.cache;

import be.fcip.cms.persistence.model.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;

@Service
public class CacheablePageUtilProviderImpl implements ICacheablePageUtilProvider {
    
    @Autowired
    private ICacheablePageProvider cachableContentProvider;

    @Override
    public boolean contentCanBeDeleted(PageEntity content, String contentDataLocale) {
        if(StringUtils.isEmpty(contentDataLocale)){
            throw new IllegalArgumentException("contentDataLocale can't be null or empty");
        }

        if (content == null || content.getId() == 0) return false;
        PageEntity temp;

        QPageEntity contentEntity = QPageEntity.pageEntity;
        QPageContentEntity qContentDataEntity = QPageContentEntity.pageContentEntity;

        for (PageEntity c : content.getPageChildren()) {
            PageEntity children = cachableContentProvider.findContent(c.getId());
            PageContentEntity contentDataDto = children.getContentMap().get(contentDataLocale);

            if (contentDataDto  == null ) {
                return false;
            }

        }
        return true;
    }

    @Override
    public Collection<PermissionEntity> getRoles(PageEntity content) {
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
            parent = cachableContentProvider.findContent(parentId);

            result.addAll(parent.getPermissions());

            parentId = parent.getPageParent() != null ? parent.getPageParent().getId() : 0;
        }
        return result;
    }

    @Override
    public boolean contentIsPrivate(PageEntity content) {
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
            parent = cachableContentProvider.findContent(parentId);
            if (parent.isMemberOnly()) {
                return true;
            }
            parentId = parent.getPageParent() != null ? parent.getPageParent().getId() : 0;
        }
    }

    @Override
    public boolean contentIsVisible(PageEntity content) {
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
            parent = cachableContentProvider.findContent(parentId);

            if (!parent.isEnabled()) {
                return false;
            }
            parentId = parent.getPageParent() != null ? parent.getPageParent().getId() : 0;
        }
    }

    @Override
    public boolean contentIsVisible(PageEntity content, PageContentEntity contentData) {
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
            parent = cachableContentProvider.findContent(parentId);
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
}
