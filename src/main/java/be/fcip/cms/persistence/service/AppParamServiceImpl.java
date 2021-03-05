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

    @Autowired
    private IAppParamRepository appParamRepository;
    @Autowired
    private ICacheableAppParamsProvider cache;

    @Override
    public Optional<AppParamEntity> findOne(String id) {
        return appParamRepository.findById(id);
    }

    @Override
    public Map<String, String> getParams() {
        return cache.getParams();
    }

    @Override
    @CacheEvict(value = "params", allEntries = true)
    public void delete(String id) {

        if(!IAppParamService.CORE_PARAMS.contains(id)) {
            appParamRepository.deleteById(id);
        }
    }

    @Override
    @CacheEvict(value = "params", allEntries = true)
    public void delete(AppParamEntity param) {
        if(!IAppParamService.CORE_PARAMS.contains(param.getId())){
            appParamRepository.delete(param);
        }
    }

    @Override
    @CacheEvict(value = "params", allEntries = true)
    public AppParamEntity save(AppParamEntity param) {
        return appParamRepository.save(param);
    }

    @Override
    @CacheEvict(value = "params", allEntries = true)
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
        return getParams().get(id);
    }

    @Override
    public String getParam(String id, String langId) {
        Map<String, String> paramsMap = getParams();
        String result = null;
        // Try to find a translated params (title is already translated)
        if(ApplicationUtils.locales.size() > 1 && !id.equals("title")){
            result = paramsMap.get(id + "_" + langId);
        }
        // Try default
        if(StringUtils.isEmpty(result)){
            result = paramsMap.get(id);
        }
        return result;
    }

    @Override
    public boolean isMaintenance() {
        return getParam("maintenance").equals("true");
    }

    @Override
    public boolean isExtranetActive() {
        return getParam(PARAM_EXTRANET_ACTIVE).equals("true");
    }

    @Override
    @CacheEvict(value = "params", allEntries = true)
    public void setMaintenance(boolean value) {
        Optional<AppParamEntity> maintenance = findOne("maintenance");
        maintenance.ifPresent((m) -> {
            m.setValue(String.valueOf(value));
            appParamRepository.save(m);
        });
    }

    @Override
    public List<String> getContactEmails() {
        String[] split = getParam(IAppParamService.PARAM_EMAIL_CONTACT).split(";");
        return Arrays.stream(split).collect(Collectors.toList());
    }

    @Override
    public List<String> getDevEmails() {
        String[] split = getParam(IAppParamService.PARAM_EMAIL_DEV).split(";");
        return Arrays.stream(split).collect(Collectors.toList());
    }

    public static Pattern PROCESS_REGEX_PATTERN = Pattern.compile("\\[([a-zA-Z]+)\\]");
    /**
     * Replace the token [xxx] by the param xxx in the map
     * @param template
     * @param lang
     * @return
     */
    @Override
    public String replaceTokenByParam(String template, String lang){
        Matcher matcher = PROCESS_REGEX_PATTERN.matcher(template);
        int count = 0;
        StringBuffer sb = null;
        while(matcher.find()) {
            count++;
            if(sb == null) sb = new StringBuffer();
            matcher.appendReplacement(sb, getParam(matcher.group(1), lang));
        }
        if(count == 0){
            return template;
        } else {
            matcher.appendTail(sb);
            return sb.toString();
        }
    }
}
