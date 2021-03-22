package be.fcip.cms.persistence.service;

import be.fcip.cms.persistence.cache.ICacheableAppParamsProvider;
import be.fcip.cms.persistence.model.AppParamEntity;
import be.fcip.cms.persistence.repository.IAppParamRepository;
import be.fcip.cms.util.ApplicationUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service("appParamService")
@Transactional
public class AppParamServiceImpl implements IAppParamService {

    @Autowired private IAppParamRepository appParamRepository;
    @Autowired private ICacheableAppParamsProvider cache;

    @Override
    public Optional<AppParamEntity> findOne(String id) {
        return appParamRepository.findById(id);
    }

    @Override
    public Map<String, String> getParamsCached() {
        return cache.getParams();
    }

    @Override
    @CacheEvict(value = "global", key= "'appParams'")
    public void delete(String id) {

        if(!IAppParamService.CORE_PARAMS.contains(id)) {
            appParamRepository.deleteById(id);
        }
    }

    @Override
    @CacheEvict(value = "global", key= "'appParams'")
    public void delete(AppParamEntity param) {
        if(!IAppParamService.CORE_PARAMS.contains(param.getId())){
            appParamRepository.delete(param);
        }
    }

    @Override
    @CacheEvict(value = "global", key= "'appParams'")
    public AppParamEntity save(AppParamEntity param) {
        return appParamRepository.save(param);
    }

    @Override
    @CacheEvict(value = "global", key= "'appParams'")
    public List<AppParamEntity> save(List<AppParamEntity> params) {
        return appParamRepository.saveAll(params);
    }

    @Override
    public List<String> getWebContentType() {
        List<String> result = new ArrayList<>();
        result.add("news");
        return result;
    }

    @Override
    public String getParam(String id) {
        return getParamsCached().get(id);
    }

    public static String getParam(String id, String langId, Map<String, String> paramsMap){
        String result = null;
        // Try to find a translated params (title is already translated)
        if(ApplicationUtils.locales.size() > 1){
            result = paramsMap.get(id + "_" + langId);
        }
        // Try default
        if(StringUtils.isEmpty(result)){
            result = paramsMap.get(id);
        }
        return result;
    }

    @Override
    public String getParam(String id, String langId) {
        return getParam(id, langId, getParamsCached());
    }

    @Override
    public List<String> getDevEmails() {
        String[] split = getParam(IAppParamService.PARAM_EMAIL_DEV).split(";");
        return Arrays.stream(split).collect(Collectors.toList());
    }
}
