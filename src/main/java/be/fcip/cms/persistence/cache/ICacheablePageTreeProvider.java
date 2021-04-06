package be.fcip.cms.persistence.cache;

import be.fcip.cms.model.MenuItem;
import be.fcip.cms.model.tree.TreeItem;
import be.fcip.cms.persistence.model.PageEntity;

import java.util.List;

public interface ICacheablePageTreeProvider {
    //List<TreeItem> getTreeItems(Long websiteId);

    List<MenuItem> getMenuItem(Long pageRootId, String lang, long depth, Long currentPageId, boolean onlyTitle, Integer rootOffset, Integer limitRoot, Long websiteId);

    String getMenu(Long pageId, String lang, long depth, Long currentPageId, boolean onlyTitle, Integer rootOffset, Integer limitRoot, Long websiteId, String ulChildrenCLass, String liChildrenClass, String linkClass);

    String getPagesTree(String lang, String type, Long website);

    String getBreadcrumb(PageEntity content, String locale, String separator, Long parendId, boolean h1);
}
