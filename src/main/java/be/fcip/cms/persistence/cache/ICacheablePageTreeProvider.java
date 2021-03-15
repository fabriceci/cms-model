package be.fcip.cms.persistence.cache;

import be.fcip.cms.model.tree.TreeItem;
import be.fcip.cms.persistence.model.PageEntity;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

public interface ICacheablePageTreeProvider {
    List<TreeItem> getTreeItems(Long websiteId);

    String getMenu(Long contentId, String lang, long depth, Long currentContentId, boolean onlyTitle, Integer rootOffset, Integer limitRoot);

    String getPagesTree(String lang, String type, Long website);

    String getBreadcrumb(PageEntity content, String locale, String separator, Long parendId, boolean h1);
}
