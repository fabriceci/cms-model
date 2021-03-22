package be.fcip.cms.pebble.view;

import be.fcip.cms.model.db.PageableResult;
import be.fcip.cms.persistence.model.PageContentEntity;
import be.fcip.cms.persistence.model.PageEntity;
import be.fcip.cms.persistence.model.PageFileEntity;
import be.fcip.cms.persistence.model.TaxonomyEntity;
import be.fcip.cms.persistence.service.IPageService;
import be.fcip.cms.util.CmsContentUtils;
import be.fcip.cms.util.CmsNumericUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class PageHelper {

    @Autowired private IPageService pageService;

    public static String humanSize(Long value){
        return FileUtils.byteCountToDisplaySize(value);
    }

    // Static Block
    public static List<PageFileEntity> filesList(PageContentEntity data, String type) {
        return CmsContentUtils.filesList(data, type);
    }

    public static Map<String, List<PageFileEntity>> filesByGroupMap(PageContentEntity data, String type){
        return CmsContentUtils.filesByGroupMap(data, type);
    }
    public static HashMap<String, Object> pageData(PageContentEntity contentData){
        HashMap<String, Object> map = CmsContentUtils.parseData(contentData.getData());
        return map;
    }

    public static HashMap<String, Object> pageData(PageEntity pageEntity, String locale){
        PageContentEntity contentData = pageEntity.getContentMap().get(locale);
        return (contentData != null) ? CmsContentUtils.parseData(contentData.getData()) : null;
    }
    // end static block

    public HashMap<String, Object> pageData(Long id){

        PageContentEntity contentData = pageService.findPageContent(id);
        return (contentData != null) ? CmsContentUtils.parseData(contentData.getData()) : null;
    }

    public HashMap<String, Object> pageData(Long id, String locale){

        PageEntity content = pageService.findPageCached(id);
        PageContentEntity contentData = content.getContentMap().get(locale);
        return (contentData != null) ? CmsContentUtils.parseData(contentData.getData()) : null;
    }

    public List<PageContentEntity> childrens(Long id, String code){
        return childrens(pageService.findPageCached(id), code);
    }

    public List<PageContentEntity> childrens(PageEntity entity, String code){
        List<PageContentEntity> result = new ArrayList<>();
        PageContentEntity data;
        for (PageEntity children : entity.getPageChildren()) {
            PageEntity childrenEntity = pageService.findPageCached(children.getId());
            data = childrenEntity.getContentMap().get(code);
            if(data != null) result.add(data);
        }
        return result;
    }

    public List<PageContentEntity> brothers(PageEntity entity, String code, boolean selfInclude){
        List<PageContentEntity> result = new ArrayList<>();
        PageEntity parent = pageService.findPageCached(entity.getPageParent().getId());
        PageContentEntity data;
        for (PageEntity c : pageService.findParents(parent)) {
            if(!selfInclude) {
                if (c.getId() == entity.getId())
                    continue;
            }
            PageEntity contentAdmin = pageService.findPageCached(c.getId());
            data = contentAdmin.getContentMap().get(code);
            if(data != null ) result.add(data);
        }
        return result;
    }

    public List<PageContentEntity> parents(PageEntity entity, String code){
        List<PageContentEntity> result = new ArrayList<>();
        if(entity.getPageParent() == null) return null;

        PageEntity parent = pageService.findPageCached(entity.getPageParent().getId());
        parent = pageService.findPageCached(parent.getPageParent().getId());

        PageContentEntity data;
        for (PageEntity c : pageService.findParents(parent)) {

            PageEntity pageParent = pageService.findPageCached(c.getId());
            data = pageParent.getContentMap().get(code);
            if(data != null) result.add(data);
        }
        return result;
    }

    public PageContentEntity nextBrother(PageEntity entity, String code) {
        PageEntity parent = pageService.findPageCached(entity.getPageParent().getId());
        PageContentEntity nextData = null;
        boolean next = false;
        PageContentEntity data;
        for (PageEntity c : pageService.findParents(parent)) {
            if (c.getId() == entity.getId()) {
                next = true;
                continue;
            }

            if (next) {
                PageEntity contentAdmin = pageService.findPageCached(c.getId());
                data = contentAdmin.getContentMap().get(code);
                if (data != null) {
                    nextData = data;
                    break;
                }
            }
        }
        return nextData;
    }

    public PageContentEntity previousBrother(PageEntity entity, String code){
        if(entity == null) return null;
        PageEntity parent = pageService.findPageCached(entity.getPageParent().getId());
        PageContentEntity previousData = null;
        PageContentEntity data;
        for (PageEntity c : pageService.findParents(parent)) {
            if(c.getId() == entity.getId()) {
                break;
            }

            PageEntity contentAdmin = pageService.findPageCached(c.getId());
            data = contentAdmin.getContentMap().get(code);
            if(data != null) previousData=data;
        }
        return previousData;
    }

    public PageContentEntity getContent(Long id, String code){
        PageEntity contentAdmin = pageService.findPageCached(id);
        if (contentAdmin == null) return null;

        return contentAdmin.getContentMap().get(code);
    }

    public PageEntity get(Long id){
        return pageService.findPageCached(id);
    }

    public PageableResult<PageEntity> find(String locale, List<TaxonomyEntity> terms, String contentType, Long pageNumber, Long limit, Boolean isPrivate){
        String result = null;

        if(terms != null && terms.size() > 0) {

            StringBuilder builder = new StringBuilder();

            for (TaxonomyEntity term : terms) {
                builder.append(term.getName()).append(",");
            }
            String builderResult = builder.toString();
            if(builderResult.length()> 1) {
                result = builderResult.substring(0, builderResult.length() - 1);
            }
        }
        Long date = null;

        return pageService.search(locale, date, null, null, null, null, result , contentType, pageNumber, limit, isPrivate);
    }

    public PageableResult<PageEntity> find(String locale, LocalDateTime begin, LocalDateTime end, String name, String type, String category, String tags, String contentType, Object pageNumber, Long limit, Boolean isPrivate){


        Long page = CmsNumericUtils.objectToLong(pageNumber);

        PageableResult<PageEntity> result = pageService.search(locale, begin, end, name, type, category, tags, contentType, page, limit, isPrivate);
        return result;
    }

    public PageableResult<PageEntity> find(String locale, String yearString, String name, String type, String category, String tags, String contentType, Object pageNumber, Long limit, Boolean isPrivate){

        Long page = CmsNumericUtils.objectToLong(pageNumber);
        if(page == null) page = 1L;
        Long yearStart = null;
        Long yearEnd = null;
        if(!StringUtils.isEmpty(yearString)){
            try {
                if(yearString.length() == 4) {
                    yearStart = Long.parseLong(yearString);
                } else {
                    yearEnd = Long.parseLong(yearString.substring(0,4));
                    yearStart = 2000L;
                }
            } catch(Exception e){
                yearStart = null;
            }
        }

        if(pageNumber == null) pageNumber = 1L;

        PageableResult<PageEntity> result = null;

        if(yearEnd != null){
            result = pageService.search(locale, yearStart, yearEnd, name, type, category, tags, contentType, page, limit, isPrivate);
        } else {
            result = pageService.search(locale, yearStart, name, type, category, tags, contentType, page, limit, isPrivate);
        }

        return result;
    }

    public PageableResult<PageEntity> find(String locale, String type, String contentType, Object pageNumber, Long limit){

        Long page = CmsNumericUtils.objectToLong(pageNumber);
        if(page == null) page = 1L;
        if(pageNumber == null) pageNumber = 1L;

        PageableResult<PageEntity> result = pageService.search(locale, type, contentType, page, limit);


        return result;
    }

    public static List<TaxonomyEntity> taxonomyByType(PageEntity content, String type){
        return CmsContentUtils.taxonomyByType(content, type);
    }

    public boolean isPrivate(PageEntity page){
        return pageService.pageIsPrivate(page);
    }

    public boolean isVisible(PageEntity page, PageContentEntity content){
        return pageService.pageIsVisible(page, content);
    }

    public boolean canBeDeleted(PageEntity page, String lang){
        return pageService.pageCanBeDeleted(page, lang);
    }


}
