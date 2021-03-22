package be.fcip.cms.persistence.service;

import be.fcip.cms.persistence.model.AppParamEntity;

import java.util.*;

public interface IAppParamService {

    /* Interface Params */
    String PARAM_EMAIL_DEV = "email_dev";

    Set<String> CORE_PARAMS = new HashSet<>(Arrays.asList(PARAM_EMAIL_DEV));

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

    List<String> getDevEmails();
}
