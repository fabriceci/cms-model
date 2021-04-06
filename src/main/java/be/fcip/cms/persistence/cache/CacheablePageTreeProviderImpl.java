package be.fcip.cms.persistence.cache;

import be.fcip.cms.model.MenuItem;
import be.fcip.cms.persistence.model.PageContentEntity;
import be.fcip.cms.persistence.model.PageEntity;
import be.fcip.cms.persistence.model.WebsiteEntity;
import be.fcip.cms.persistence.repository.IPageRepository;
import be.fcip.cms.persistence.service.IWebsiteService;
import be.fcip.cms.util.*;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.List;

@Service
@Transactional
public class CacheablePageTreeProviderImpl implements ICacheablePageTreeProvider {

    @Autowired private ICacheablePageProvider cachableContentProvider;
    @Autowired private IPageRepository pageRepository;
    @Autowired private IWebsiteService websiteService;

    
    @Value("${cms.lang.default}")
    private String defautLang;

    private final static int MAX_EXPANDED_TREE_LEVEL = 3; // 0 based
    
    
    /* LIE A LA VERSION REACT
    @Override
    public List<TreeItem> getTreeItems(Long websiteId) {


        // Use first level cache to do all the tree in only 2 requests
        Set<PageEntity> pages = pageRepository.findAllPages(websiteId); // force first level cache
        List<PageEntity> roots = pageRepository.findByPageParentIsNullOrderAndWebsiteIdByPositionAsc(websiteId);

        List<TreeItem> result = new ArrayList<>();
        convertList(result, roots);

        return result;
    }

    private void convertList(List<TreeItem> result, List<PageEntity> entities){
        for (PageEntity entity : entities) {
            TreeItem item = new TreeItem();
            // title
            if(entity.getContentMap().size() == 0){
                item.setTitle("Error, no content");
            }else if(entity.getContentMap().containsKey(defautLang)){
                item.setTitle(entity.getContentMap().get(defautLang).getTitle());
            } else {
                Map.Entry<String, PageContentEntity> next = entity.getContentMap().entrySet().iterator().next();
                item.setTitle(next.getValue().getTitle());
            }
            item.setKey(entity.getId());
            item.setType(entity.getPageType());
            if(!entity.getPageChildren().isEmpty()){
                convertList(item.getChildren(), entity.getPageChildren());
            }
            result.add(item);
        }
    }
    */

    /**
     *
     * @param parentId the parrentPage Id, if null, it's a root page
     * @param lang the current lang
     * @param depth how depth
     * @param currentPageId the current page
     * @param onlyTitle if true, return the page title whatever there is or not menu item title
     * @param rootOffset skip n first result
     * @param limitRoot limit the number of result
     * @param websiteId the websiteID
     * @return
     */
    @Override
    @Cacheable(value = "pageGlobal")
    public List<MenuItem> getMenuItem(Long parentId, String lang, long depth, Long currentPageId, boolean onlyTitle, Integer rootOffset, Integer limitRoot, Long websiteId){
        List<PageEntity> rootsByContentIdCustom = pageRepository.findRootsByPageIdCustom(parentId, lang,
                true, websiteId);
        List<PageEntity> roots = new ArrayList<>();
        int cpt = 0;
        for (PageEntity contentEntity : rootsByContentIdCustom) {
            cpt++;
            if(rootOffset != null){
                if(cpt <= rootOffset){
                    continue;
                }
            }
            roots.add(new PageEntity(contentEntity.getId()));

            if(limitRoot != null && roots.size() == limitRoot){
                break;
            }
        }

        List<MenuItem> result = new ArrayList<>();
        return buildMenuItem(result, roots, lang, depth, currentPageId, onlyTitle);
    }
    private List<MenuItem> buildMenuItem(List<MenuItem> result, List<PageEntity> pages, String locale, long depth,
                               Long currentContentId, boolean onlyTitle) {

        for (PageEntity p : pages) {
            PageEntity content = cachableContentProvider.findContent(p.getId());

            if (!content.isEnabled() || !content.isMenuItem()) {
                continue;
            }

            MenuItem item = new MenuItem();
            item.setPageId(p.getId());
            item.setMenuClass(p.getMenuClass());
            if(currentContentId != null) item.setActive(content.getId() == currentContentId);

            if(content.getTemplate().getId() == CmsUtils.TEMPLATE_FOLDER_ID){
                item.setType("folder");
            } else if (content.getTemplate().getId()  == CmsUtils.TEMPLATE_LINK_ID){
                item.setType("link");
            } else {
                item.setType("page");
            }

            if (!content.getContentMap().isEmpty()) {
                PageContentEntity data = content.getContentMap().get(locale);
                item.setDataId(data.getId());
                if (data != null && data.isEnabled()){
                    item.setTitle(StringUtils.isEmpty(data.getMenuTitle()) || onlyTitle ? data.getTitle() : data.getMenuTitle());
                    item.setSlug(data.getComputedSlug());
                }
            }

            // children
            List<PageEntity> childrens = new ArrayList<>();
            for (PageEntity c : content.getPageChildren()) {
                PageEntity contentChildren = cachableContentProvider.findContent(c.getId());
                if (contentChildren.isEnabled() && contentChildren.isMenuItem()) {
                    childrens.add(contentChildren);
                }
            }

            if (childrens.size() > 0 && depth != 0) {
                List<MenuItem> childResult = new ArrayList<>();
                item.setChildren(buildMenuItem(childResult, childrens, locale, depth - 1, currentContentId, onlyTitle));
            }

            // skip empty item
            if(item.getChildren() == null && item.getTitle() == null){
                continue;
            }
            result.add(item);
        }
        return result;
    }


    @Override
    @Cacheable(value = "pageGlobal")
    public String getMenu(Long parentId, String lang, long depth, Long currentPageId, boolean onlyTitle, Integer rootOffset, Integer limitRoot, Long websiteId, String ulChildrenCLass, String liChildrenClass, String linkClass) {
        List<PageEntity> rootsByContentIdCustom = pageRepository.findRootsByPageIdCustom(parentId, lang,
                true, websiteId);
        List<PageEntity> roots = new ArrayList<>();
        int cpt = 0;
        for (PageEntity contentEntity : rootsByContentIdCustom) {
            cpt++;
            if(rootOffset != null){
                if(cpt <= rootOffset){
                    continue;
                }
            }
            roots.add(new PageEntity(contentEntity.getId()));

            if(limitRoot != null && roots.size() == limitRoot){
                break;
            }
        }

        List<MenuItem> result = new ArrayList<>();
        buildMenuItem(result , roots, lang, depth, currentPageId, onlyTitle);

        if(ulChildrenCLass == null){
            ulChildrenCLass = "";
        }
        if(liChildrenClass == null){
            liChildrenClass = "has_children";
        }
        if(linkClass == null){
            linkClass = "";
        }
        StringBuilder sb = new StringBuilder();
        buildHtmlNavMenu(result, sb, ulChildrenCLass, liChildrenClass, linkClass);
        return sb.toString();
    }

    private String buildHtmlNavMenu(List<MenuItem> pages, StringBuilder sb, String childrenUlClass, String childrenLiClass, String linkClass) {

        for (MenuItem p : pages) {

            if (p.isActive()) {
                sb.append("<li class='active'>");
            } else if (p.getChildren() != null && p.getChildren().size() > 0) {
                sb.append(String.format("<li class='%s'>", childrenLiClass));
            } else {
                sb.append("<li>");
            }

            if (!p.getType().equals("folder")) {
                sb.append("<a ");
                if (p.isActive() || !StringUtils.isEmpty(p.getMenuClass())) {
                    sb.append("class='");
                    if (p.isActive()) {
                        sb.append("active ");
                    }
                    if(!StringUtils.isEmpty(linkClass)){
                        sb.append(linkClass).append(" ");
                    }
                    if (!StringUtils.isEmpty(p.getMenuClass())) {
                        sb.append(p.getMenuClass());
                    }
                    sb.append("' ");
                }
                sb.append("href=\"");
                if (p.getType().equals("link")) {
                    sb.append(p.getTitle());
                    sb.append("\" target=\"_blank\">");
                } else {
                    sb.append(p.getSlug());
                    sb.append("\">");
                }

                if (!StringUtils.isEmpty(p.getMenuContent())) {
                    sb.append(p.getMenuContent());
                    if (!p.isMenuContentOnly()) {
                        sb.append(p.getTitle());
                    }
                } else {
                    sb.append(p.getTitle());
                }
                sb.append("</a>");
            } else {
                sb.append("<a>").append(p.getTitle()).append("</a>");
            }

            if (p.getChildren() != null && p.getChildren().size() > 0) {

                sb.append(String.format("<ul class='%s'>", childrenUlClass));
                buildHtmlNavMenu(p.getChildren(), sb, childrenUlClass, childrenLiClass, linkClass);
                sb.append("</ul>");
            }
            sb.append("</li>");
        }

        return sb.toString();
    }


    @Override
    @Cacheable(value = "pageGlobal")
    public String getPagesTree(String lang, String type, Long websiteId) {

        // Use first level cache to do all the tree in only 2 requests
        Set<PageEntity> pages = pageRepository.findAllPages(websiteId); // force first level cache
        List<PageEntity> roots = pageRepository.findByPageParentIsNullOrderAndWebsiteIdByPositionAsc(websiteId);

        // List<PageEntity> roots =
        // contentRepository.findRootsByContentIdCustom(null, lang, false);
        StringBuilder sb = new StringBuilder();

        sb.append("[");
        buildJsonTree(roots, sb, true, 0, lang, type);
        sb.append("]");
        return sb.toString();
    }

    @Override
    @Cacheable(value = "pageGlobal")
    public String getBreadcrumb(PageEntity content, String locale, String separator, Long parendId, boolean h1) {
        if (content == null)
            return null;
        StringBuilder sb = new StringBuilder();
        // sb.append("<ul class=\"main-breadcrumb\">");
        if (separator == null)
            separator = "";

        if (parendId != null) {

            buildBreadCrumb(parendId, locale, sb, false, separator, h1);
            PageContentEntity contentData = content.getContentMap().get(locale);

            String url = contentData.getComputedSlug();

            sb.append("<li class=\"current\"><a href=\"" + url + "\">" + contentData.getTitle() + "</a></li>");

        } else {
            buildBreadCrumb(content.getId(), locale, sb, true, separator, h1);
        }
        // sb.append("</ul>");
        return sb.toString();
    }

    private void buildBreadCrumb(Long contentId, String locale, StringBuilder sb, boolean isCurrentPage,
                                 String seperator, boolean h1) {

        PageEntity content = cachableContentProvider.findContent(contentId);
        WebsiteEntity website = websiteService.findAllCached().get(content.getWebsite().getId());
        PageContentEntity data = content.getContentMap().get(locale);
        HashMap<String, Object> map = CmsContentUtils.parseData(data.getData());
        String title =  data.getTitle();
        if(h1){
            if(!StringUtils.isEmpty((String) map.get("seo_h1"))){
                title = (String)map.get("seo_h1");
            } else if(!StringUtils.isEmpty(website.findTranslatableProperty("seo_h1", locale))){
                String seo_h1 = website.findTranslatableProperty("seo_h1", locale);
                Map<String, String> seoMap = website.getSeoMap(locale);
                seoMap.put("title", title);
                title = CmsTokenUtils.parse(seo_h1, seoMap);
            }
        }

        if (content.getPageParent() != null) {
            buildBreadCrumb(content.getPageParent().getId(), locale, sb, false, seperator, h1);
            appendLi(sb, data, isCurrentPage, title, seperator, h1);
        } else {
            appendLi(sb, data, isCurrentPage, title, seperator, h1);
            if (isCurrentPage) {
                isCurrentPage = false;
            }
        }
    }

    private static void appendLi(StringBuilder sb, PageContentEntity data, boolean isCurrentPage, String title,
                                 String seperator, boolean h1) {
        if (isCurrentPage) {
            isCurrentPage = false;
            sb.append("<li class=\"current\">");
            if (h1) {
                sb.append("<h1>");
            }
            sb.append("<a>");
            sb.append(title);
            sb.append("</a>");
            if (h1) {
                sb.append("</h1>");
            }
            sb.append("</li>");
        } else {
            String url = data.getComputedSlug();
            // TO DO : FEANTSA SEARCH - generic
            url = url.replace("/feantsaresearch", "");
            sb.append("<li><a href=\"" + url + "\" >");
            sb.append(title);
            sb.append("</a>" + seperator + "</li>");
        }
    }


    private String buildJsonTree(List<PageEntity> pages, StringBuilder sb, boolean first, int level, String locale,
                                 String type) {

        for (PageEntity p : pages) {

            boolean isFolderPage = false;
            boolean isInvisible = false;
            boolean isLocked = false;

            if (!first) {
                sb.append(",");
            }
            first = false;

            String title = "";

            if(StringUtils.isEmpty(locale) || !ApplicationUtils.locales.contains(LocaleUtils.toLocale(locale))){
                locale = ApplicationUtils.defaultLocale.toString();
            }

            PageContentEntity contentDataEntity = p.getContentMap().get(locale);
            if (contentDataEntity == null)
                break;
            if (type != null && type.equals("full")) {
                title = p.getContentMap().get(locale) + " [[" + p.getContentMap().get(locale).getTitle() + "]]";
            } else {
                PageContentEntity pce = p.getContentMap().get(locale);
                title = StringUtils.isEmpty(pce.getMenuTitle()) ? pce.getTitle() : pce.getMenuTitle();
            }

            sb.append("{ \"title\": \"").append(title.replace("\"", "\\\"")).append("\", \"key\": \"").append(p.getId())
                    .append("\"");

            List<PageEntity> childrens = p.getPageChildren();

            if (childrens.size() > 0) {
                isFolderPage = true;
                if (level <= MAX_EXPANDED_TREE_LEVEL) {
                    sb.append(", \"expanded\":true");
                }
                sb.append(", \"folder\":true");
                // This is bad :(
                Collections.sort(childrens, (p1, p2) -> Integer.compare(p1.getPosition(), p2.getPosition()));

                sb.append(", \"children\" : [");
                buildJsonTree(childrens, sb, true, level + 1, locale, type);
                sb.append("]");
            }

            isLocked = contentIsPrivate(p);
            isInvisible = !contentIsVisible(p);
            String icon = "";
            String name = String.valueOf(p.getId());
            boolean isFolder = p.getTemplate().getId() == CmsUtils.TEMPLATE_FOLDER_ID;
            boolean isLink = p.getTemplate().getId() == CmsUtils.TEMPLATE_LINK_ID;

            if (isFolder) {
                icon = "folder.png";
            } else if (isLink) {
                icon = "link.png";
            } else if (!isFolderPage && !isLocked && !isInvisible) {
                icon = "file.png";
            } else if (!isFolderPage && !isLocked && isInvisible) {
                icon = "hidden.png";
            } else if (!isFolderPage && isLocked && !isInvisible) {
                icon = "lock.png";
            } else if (!isFolderPage && isLocked && isInvisible) {
                icon = "lock-invisible.png";
            } else if (isFolderPage && !isLocked && !isInvisible) {
                icon = "folder-page.png";
            } else if (isFolderPage && !isLocked && isInvisible) {
                icon = "folder-invisible.png";
            } else if (isFolderPage && isLocked && !isInvisible) {
                icon = "folder-locked.png";
            } else if (isFolderPage && isLocked && isInvisible) {
                icon = "folder-invisble-locked.png";
            }

            sb.append(",\"icon\": \"").append(WebConfigConstants.RESOURCES_LOCATION + "cms/img/tree/" + icon + "\"");

            sb.append("}");
        }

        return sb.toString();
    }

    private static boolean contentIsPrivate(PageEntity content) {
        // force to call cache
        PageEntity parent = null;

        if (content == null || content.getId() == 0)
            return false;

        if (content.isMemberOnly())
            return true;

        parent = content.getPageParent();
        while (true) {
            // no more parent
            if (parent == null) {
                return false;
            }
            if (parent.isMemberOnly()) {
                return true;
            }
            parent = parent.getPageParent();
        }
    }

    private static boolean contentIsVisible(PageEntity content) {
        // force to call cache
        PageEntity parent = null;

        if (content == null || content.getId() == 0 || !content.isEnabled())
            return false;

        parent = content.getPageParent();
        while (true) {
            // no more parent
            if (parent == null) {
                return true;
            }
            if (!parent.isEnabled()) {
                return false;
            }
            parent = parent.getPageParent();
        }
    }
}


