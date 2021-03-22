package be.fcip.cms.persistence.service;

import be.fcip.cms.persistence.model.PageEntity;
import be.fcip.cms.persistence.model.WebsiteEntity;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface IWebsiteService {
    String PARAM_SEO_DESCRIPTION = "seo_description";
    String PARAM_SEO_TAGS = "seo_tags";
    String PARAM_SEO_TITLE = "seo_title";
    String PARAM_SEO_H1 = "seo_h1";
    String PARAM_SEO_IMAGE = "seo_image";
    String PARAM_SITENAME = "sitename";
    List<String> SEO_FIELD_KEYS = Arrays.asList(PARAM_SEO_DESCRIPTION, PARAM_SEO_TAGS, PARAM_SEO_TITLE, PARAM_SEO_H1, PARAM_SEO_IMAGE, PARAM_SITENAME);


    WebsiteEntity save(WebsiteEntity website);
    Map<Long, WebsiteEntity> findAllCached();
    Optional<WebsiteEntity> findById(Long id);
    void delete(Long id);

    WebsiteEntity getWebsiteFromUrl(HttpServletRequest request);
    WebsiteEntity getWebsiteFromUrl(String path);
    WebsiteEntity getWebsiteFromPage(PageEntity page);
}
