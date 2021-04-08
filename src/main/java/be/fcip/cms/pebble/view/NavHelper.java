package be.fcip.cms.pebble.view;

import be.fcip.cms.model.MenuItem;
import be.fcip.cms.persistence.model.PageEntity;
import be.fcip.cms.persistence.service.IPageService;
import be.fcip.cms.util.CmsNumericUtils;
import be.fcip.cms.util.CmsTypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class NavHelper {

    @Autowired private IPageService contentService;

    public String breadcrumb(PageEntity content, String locale, String seperator, Object parentId){
        Long parentIdLong = CmsNumericUtils.objectToLong(parentId);
        return contentService.getBreadcrumbCached(content, locale, seperator, parentIdLong, false);
    }

    public String breadcrumb(PageEntity content, String locale, String seperator, Object parentId, boolean h1){
        Long parentIdLong = CmsNumericUtils.objectToLong(parentId);
        return contentService.getBreadcrumbCached(content, locale, seperator, parentIdLong, h1);
    }


    public List<MenuItem> menuItem(Map<Object, Object> params){
        Long parentId = CmsTypeUtils.toLong(params.get("parentId"));
        String locale =  CmsTypeUtils.toLocale(params.get("locale"), LocaleContextHolder.getLocale()).toString();
        long depth = CmsTypeUtils.toLong(params.get("depth"), 0);
        Long currentPageId =  CmsTypeUtils.toLong(params.get("currentPageId"));
        Boolean onlyTitle =  CmsTypeUtils.toBoolean(params.get("contentId"), false);
        Integer offset =  CmsTypeUtils.toInteger(params.get("offset"));
        Integer limit = CmsTypeUtils.toInteger(params.get("limit"));
        Long websiteId =  CmsTypeUtils.toLong(params.get("websiteId"));
        if(websiteId == null){
            websiteId = (Long)RequestContextHolder.currentRequestAttributes().getAttribute("websiteId", RequestAttributes.SCOPE_REQUEST);
        }
        return contentService.getNavItemCached(parentId, locale, depth, currentPageId, onlyTitle, offset, limit, websiteId);
    }
    public String menu() {
        return menu(new HashMap<>());
    }
    public String menu(Map<Object, Object> params){

        Long parentId = CmsTypeUtils.toLong(params.get("parentId"));
        String locale =  CmsTypeUtils.localeToString(params.get("locale")).toString();
        Long depth = CmsTypeUtils.toLong(params.get("depth"));
        Long currentPageId =  CmsTypeUtils.toLong(params.get("currentPageId"));
        Boolean onlyTitle =  CmsTypeUtils.toBoolean(params.get("contentId"), false);
        Integer offset =  CmsTypeUtils.toInteger(params.get("offset"));
        Integer limit = CmsTypeUtils.toInteger(params.get("limit"));
        Long websiteId =  CmsTypeUtils.toLong(params.get("websiteId"));
        String ulChildrenClass = CmsTypeUtils.toString(params.get("ulChildrenClass"));
        String liChildrenClass = CmsTypeUtils.toString(params.get("liChildrenClass"));
        String linkClass = CmsTypeUtils.toString(params.get("linkClass"));
        if(websiteId == null){
            websiteId = (Long)RequestContextHolder.currentRequestAttributes().getAttribute("websiteId", RequestAttributes.SCOPE_REQUEST);
        }
        return contentService.getNavCached(parentId, locale, depth, currentPageId, onlyTitle, offset, limit, websiteId, ulChildrenClass, liChildrenClass, linkClass);
    }
}
