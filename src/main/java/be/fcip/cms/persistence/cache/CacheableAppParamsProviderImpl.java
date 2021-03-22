package be.fcip.cms.persistence.cache;

import be.fcip.cms.persistence.model.AppParamEntity;
import be.fcip.cms.persistence.repository.IAppParamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CacheableAppParamsProviderImpl implements ICacheableAppParamsProvider {

    @Autowired private IAppParamRepository appParamRepository;

    @Override
    @Cacheable(value = "global", key= "'appParams'")
    public Map<String, String> getParams() {
        LinkedHashMap<String, String> map = appParamRepository.findAll(Sort.by(Sort.Direction.ASC, "id")).stream().collect(
                Collectors.toMap(AppParamEntity::getId, e -> Optional.ofNullable(e.getValue()).orElse(""), (x, y) -> y, LinkedHashMap::new)
        );
        return Collections.unmodifiableMap(map);
    }
}
