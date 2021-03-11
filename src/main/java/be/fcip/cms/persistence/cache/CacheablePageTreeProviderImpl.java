package be.fcip.cms.persistence.cache;

import be.fcip.cms.model.tree.TreeItem;
import be.fcip.cms.persistence.model.PageContentEntity;
import be.fcip.cms.persistence.model.PageEntity;
import be.fcip.cms.persistence.repository.IPageRepository;
import be.fcip.cms.persistence.service.IAppParamService;
import be.fcip.cms.util.ApplicationUtils;
import be.fcip.cms.util.CmsContentUtils;
import be.fcip.cms.util.CmsUtils;
import be.fcip.cms.util.WebConfigConstants;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class CacheablePageTreeProviderImpl implements ICacheablePageTreeProvider {

    @Autowired
    private ICacheablePageProvider cachableContentProvider;

    @Autowired
    private IPageRepository pageRepository;

    @Autowired
    private IAppParamService appParamService;

    
    @Value("${cms.lang.default}")
    private String defautLang;

    private final static int MAX_EXPANDED_TREE_LEVEL = 3; // 0 based
    
    
    // LIE A LA VERSION REACT
    @Override
    public List<TreeItem> getTreeItems(Long websiteId) {


        // Use first level cache to do all the tree in only 2 requests
        Set<PageEntity> pages = pageRepository.findAllPages(); // force first level cache
        List<PageEntity> roots = pageRepository.findByPageParentIsNullOrderByPositionAsc();

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
    // ---

    @Override
    @Cacheable(value = "pageGlobal")
    public String getMenu(Long contentId, String lang, long depth, Long currentContentId, boolean onlyTitle, Integer rootOffset, Integer limitRoot) {
        List<PageEntity> rootsByContentIdCustom = pageRepository.findRootsByContentIdCustom(contentId, lang,
                true);
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


        StringBuilder sb = new StringBuilder();
        buildNavMenu(roots, sb, lang, depth, currentContentId, onlyTitle);
        return sb.toString();
    }

    @Override
    @Cacheable(value = "pageGlobal")
    public String getPagesTree(String lang, String type) {

        // Use first level cache to do all the tree in only 2 requests
        Set<PageEntity> pages = pageRepository.findAllPages(); // force first level cache
        List<PageEntity> roots = pageRepository.findByPageParentIsNullOrderByPositionAsc();

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
        PageContentEntity data = content.getContentMap().get(locale);
        HashMap<String, Object> map = CmsContentUtils.parseData(data.getData());
        String title =  data.getTitle();
        if(h1){
            if(!StringUtils.isEmpty((String) map.get("seo_h1"))){
                title = (String)map.get("seo_h1");
            } else if(!StringUtils.isEmpty(appParamService.getParam("seo_h1", locale))){
                String seo_h1 = appParamService.getParam("seo_h1", locale);
                appParamService.getParams().put("title", title);
                title = appParamService.replaceTokenByParam(seo_h1, locale);
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

    private String buildNavMenu(List<PageEntity> pages, StringBuilder sb, String locale, long depth,
                                Long currentContentId, boolean onlyTitle) {

        boolean isFolder = false;
        boolean isLink = false;
        boolean isCurrentPage = false;
        for (PageEntity p : pages) {
            PageEntity content = cachableContentProvider.findContent(p.getId());

            if (!content.isEnabled() || !content.isMenuItem()) {
                continue;
            }

            isFolder = content.getTemplate().getId() == CmsUtils.TEMPLATE_FOLDER_ID;
            isLink = content.getTemplate().getId()  == CmsUtils.TEMPLATE_LINK_ID;

            // children
            List<PageEntity> childrens = new ArrayList<>();
            for (PageEntity c : content.getPageChildren()) {
                PageEntity contentChildren = cachableContentProvider.findContent(c.getId());
                if (contentChildren.isEnabled() && contentChildren.isMenuItem()) {
                    childrens.add(contentChildren);
                }
            }

            // current page
            if (currentContentId != null) {
                isCurrentPage = content.getId() == currentContentId;

                if (childrens.size() > 0) {
                    for (PageEntity children : childrens) {
                        if (children.getId() == currentContentId) {
                            isCurrentPage = true;
                            break;
                        }
                    }
                }
            }
            if (isCurrentPage) {
                sb.append("<li class='active'>");
            } else if (childrens.size() > 0) {
                sb.append("<li class='dropdown'>");
            } else {
                sb.append("<li>");
            }

            if (!content.getContentMap().isEmpty()) {
                PageContentEntity data = content.getContentMap().get(locale);

                if (data == null)
                    break;

                String menuTitle = StringUtils.isEmpty(data.getMenuTitle()) || onlyTitle ? data.getTitle() : data.getMenuTitle();

                if (!isFolder) {
                    sb.append("<a ");
                    if (isCurrentPage || !StringUtils.isEmpty(content.getMenuClass())) {
                        sb.append("class='");
                        // Todo: remove extra space
                        if (isCurrentPage) {
                            sb.append("active ");
                        }
                        if (!StringUtils.isEmpty(content.getMenuClass())) {
                            sb.append(content.getMenuClass());
                        }
                        sb.append("' ");
                    }
                    if (isCurrentPage) {
                        sb.append("class='active' ");
                    }
                    sb.append("href=\"");
                    if (isLink) {
                        sb.append((String) CmsContentUtils.parseData(data.getData()).get("_text"));
                        sb.append("\" target=\"_blank\">");
                    } else {
                        String url = data.getComputedSlug();
                        sb.append(url);
                        sb.append("\">");
                    }

                    if (!StringUtils.isEmpty(content.getMenuContent())) {
                        sb.append(content.getMenuContent());
                        if (!content.isMenuContentOnly()) {
                            sb.append(menuTitle);
                        }
                    } else {
                        sb.append(menuTitle);
                    }
                    sb.append("</a>");
                } else {
                    sb.append("<a>").append(menuTitle).append("</a>");
                }

                if (childrens.size() > 0 && depth != 0) {

                    sb.append("<ul class='main-menu-children sub-menu'>");
                    buildNavMenu(childrens, sb, locale, depth - 1, currentContentId, onlyTitle);
                    sb.append("</ul>");
                }
                sb.append("</li>");
            }
        }

        return sb.toString();
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


