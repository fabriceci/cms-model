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
public class ContentHelper {

    @Autowired
    private IPageService contentService;

    public static String humanSize(Long value){
        return FileUtils.byteCountToDisplaySize(value);
    }

    // Static Block
    public static List<PageFileEntity> getFilesList(PageContentEntity data, String type) {
        return CmsContentUtils.filesList(data, type);
    }

    public static Map<String, List<PageFileEntity>> getFilesByGroupMap(PageContentEntity data, String type){
        return CmsContentUtils.filesByGroupMap(data, type);
    }
    public static HashMap<String, Object> getPageData(PageContentEntity contentData){
        HashMap<String, Object> map = CmsContentUtils.parseData(contentData.getData());
        return map;
    }

    public static HashMap<String, Object> getPageData(PageEntity content, String locale){
        PageContentEntity contentData = content.getContentMap().get(locale);
        return (contentData != null) ? CmsContentUtils.parseData(contentData.getData()) : null;
    }
    // end static block

    public HashMap<String, Object> getPageData(Long id){

        PageContentEntity contentData = contentService.findContentData(id);
        return (contentData != null) ? CmsContentUtils.parseData(contentData.getData()) : null;
    }

    public HashMap<String, Object> getPageData(Long id, String locale){

        PageEntity content = contentService.findContent(id);
        PageContentEntity contentData = content.getContentMap().get(locale);
        return (contentData != null) ? CmsContentUtils.parseData(contentData.getData()) : null;
    }

    public List<PageContentEntity> getChildrensData(Long id, String code){
        return getChildrensData(contentService.findContent(id), code);
    }

    public List<PageContentEntity> getChildrensData(PageEntity entity, String code){
        List<PageContentEntity> result = new ArrayList<>();
        PageContentEntity data;
        for (PageEntity children : entity.getPageChildren()) {
            PageEntity contentAdmin = contentService.findContent(children.getId());
            data = contentAdmin.getContentMap().get(code);
            if(data != null) result.add(data);
        }
        return result;
    }

    public List<PageContentEntity> getBrothersData(PageEntity entity, String code, boolean selfInclude){
        List<PageContentEntity> result = new ArrayList<>();
        PageEntity parent = contentService.findContent(entity.getPageParent().getId());
        PageContentEntity data;
        for (PageEntity c : contentService.findParents(parent)) {
            if(!selfInclude) {
                if (c.getId() == entity.getId())
                    continue;
            }
            PageEntity contentAdmin = contentService.findContent(c.getId());
            data = contentAdmin.getContentMap().get(code);
            if(data != null ) result.add(data);
        }
        return result;
    }

    public List<PageContentEntity> getParentsData(PageEntity entity, String code){
        List<PageContentEntity> result = new ArrayList<>();
        if(entity.getPageParent() == null) return null;

        PageEntity parent = contentService.findContent(entity.getPageParent().getId());
        parent = contentService.findContent(parent.getPageParent().getId());

        PageContentEntity data;
        for (PageEntity c : contentService.findParents(parent)) {

            PageEntity contentAdmin = contentService.findContent(c.getId());
            data = contentAdmin.getContentMap().get(code);
            if(data != null) result.add(data);
        }
        return result;
    }

    public PageContentEntity getNextBrotherData(PageEntity entity, String code) {
        PageEntity parent = contentService.findContent(entity.getPageParent().getId());
        PageContentEntity nextData = null;
        boolean next = false;
        PageContentEntity data;
        for (PageEntity c : contentService.findParents(parent)) {
            if (c.getId() == entity.getId()) {
                next = true;
                continue;
            }

            if (next) {
                PageEntity contentAdmin = contentService.findContent(c.getId());
                data = contentAdmin.getContentMap().get(code);
                if (data != null) {
                    nextData = data;
                    break;
                }
            }
        }
        return nextData;
    }

    public PageContentEntity getPreviousBrotherData(PageEntity entity, String code){
        if(entity == null) return null;
        PageEntity parent = contentService.findContent(entity.getPageParent().getId());
        PageContentEntity previousData = null;
        PageContentEntity data;
        for (PageEntity c : contentService.findParents(parent)) {
            if(c.getId() == entity.getId()) {
                break;
            }

            PageEntity contentAdmin = contentService.findContent(c.getId());
            data = contentAdmin.getContentMap().get(code);
            if(data != null) previousData=data;
        }
        return previousData;
    }

    public PageContentEntity getContentData(Long id, String code){
        PageEntity contentAdmin = contentService.findContent(id);
        if (contentAdmin == null) return null;

        return contentAdmin.getContentMap().get(code);
    }

    public PageEntity getContent(Long id){
        return contentService.findContent(id);
    }

    public PageableResult<PageEntity> findWebContent(String locale, List<TaxonomyEntity> terms, String contentType, Long pageNumber, Long limit, Boolean isPrivate){
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

        return contentService.findWebContent(locale, date, null, null, null, null, result , contentType, pageNumber, limit, isPrivate);
    }

    public PageableResult<PageEntity> findWebContent(String locale, LocalDateTime begin, LocalDateTime end, String name, String type, String category, String tags, String contentType, Object pageNumber, Long limit, Boolean isPrivate){


        Long page = CmsNumericUtils.objectToLong(pageNumber);

        PageableResult<PageEntity> result = contentService.findWebContent(locale, begin, end, name, type, category, tags, contentType, page, limit, isPrivate);
        return result;
    }

    public PageableResult<PageEntity> findWebContent(String locale, String yearString, String name, String type, String category, String tags, String contentType, Object pageNumber, Long limit, Boolean isPrivate){

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
            result = contentService.findWebContent(locale, yearStart, yearEnd, name, type, category, tags, contentType, page, limit, isPrivate);
        } else {
            result = contentService.findWebContent(locale, yearStart, name, type, category, tags, contentType, page, limit, isPrivate);
        }

        return result;
    }

    public PageableResult<PageEntity> findWebContent(String locale, String type, String contentType, Object pageNumber, Long limit){

        Long page = CmsNumericUtils.objectToLong(pageNumber);
        if(page == null) page = 1L;
        if(pageNumber == null) pageNumber = 1L;

        PageableResult<PageEntity> result = contentService.findWebContent(locale, type, contentType, page, limit);


        return result;
    }

    public static List<TaxonomyEntity> taxonomyByType(PageEntity content, String type){
        return CmsContentUtils.taxonomyByType(content, type);
    }

}
