package be.fcip.cms.persistence.service;

import be.fcip.cms.persistence.model.AppParamEntity;

import java.util.*;

public interface IAppParamService {

    /* Interface Params */

    String PARAM_MAINTENANCE = "maintenance";
    String PARAM_SEO_DESCRIPTION = "seo_description";
    String PARAM_SEO_TAGS = "seo_tags";
    String PARAM_SEO_TITLE = "seo_title";
    String PARAM_SEO_H1 = "seo_h1";
    String PARAM_SEO_IMAGE = "seo_image";
    String PARAM_SITE_NAME = "sitename";
    String PARAM_SITE_URL = "url";
    String PARAM_EMAIL_CONTACT = "email_contact";
    String PARAM_EMAIL_FROM_ADDRESS = "email_from_address";
    String PARAM_EMAIL_FROM_NAME = "email_from_name";
    String PARAM_EMAIL_DEV = "email_dev";

    Set<String> CORE_PARAMS = new HashSet<>(Arrays.asList(
            PARAM_MAINTENANCE, PARAM_SEO_DESCRIPTION, PARAM_SEO_TAGS, PARAM_SEO_TITLE, PARAM_SITE_NAME, PARAM_SEO_H1, PARAM_SEO_IMAGE,
            PARAM_EMAIL_CONTACT, PARAM_EMAIL_FROM_ADDRESS, PARAM_EMAIL_FROM_NAME, PARAM_EMAIL_DEV, PARAM_SITE_URL));

    /* Interface Methods */
    default boolean isCoreParam(String param){ return CORE_PARAMS.contains(param);}
    default Set<String> getCoreParams(){ return CORE_PARAMS; }

    Optional<AppParamEntity> findOne(String id);
    Map<String, String> getParamsCached();
    void delete(String id);
    void delete(AppParamEntity param);
    AppParamEntity save(AppParamEntity param);
    List<AppParamEntity> save(List<AppParamEntity> params);

    List<String> getWebContentType();

    String getParam(String id);

    String getParam(String id, String langId);

    String replaceTokenByParam(String template, String lang);

    boolean isMaintenance();

    void setMaintenance(boolean value);
    List<String> getContactEmails();

    List<String> getDevEmails();
}
