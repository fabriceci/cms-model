package be.fcip.cms.persistence.service;

import be.fcip.cms.persistence.cache.ICacheableTemplateProvider;
import be.fcip.cms.persistence.model.CmsFieldEntity;
import be.fcip.cms.persistence.repository.ICmsFieldRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class CmsFieldServiceImpl implements ICmsFieldService  {

    @Autowired private ICmsFieldRepository cmsFieldRepository;
    @Autowired private ICacheableTemplateProvider cacheableTemplateProvider;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "global", key = "'allCmsFields'"),
            @CacheEvict(value = "pebble", key = "'field_' + #field.id")
    })
    public CmsFieldEntity saveCmsField(CmsFieldEntity field) {
        return cmsFieldRepository.save(field);
    }

    @Override
    public CmsFieldEntity findCmsField(Long id) {
        return cmsFieldRepository.findById(id).orElse(null);
    }

    @Override
    public String jsonCmsField() {
        List<CmsFieldEntity> fieldEntities = cmsFieldRepository.findAll();
        JsonArrayBuilder data = Json.createArrayBuilder();
        JsonObjectBuilder row;
        // reload tree like this : table.ajax.reload()
        for (CmsFieldEntity f : fieldEntities) {
            row = Json.createObjectBuilder();
            row.add("DT_RowData", Json.createObjectBuilder().add("id", f.getId()));
            row.add("name", StringUtils.trimToEmpty(f.getName()));
            row.add("description", StringUtils.trimToEmpty(f.getDescription()));
            row.add("deletable", f.isDeletable());
            data.add(row);
        }

        return Json.createObjectBuilder().add("data", data).build().toString();
    }

    @Override
    public List<CmsFieldEntity> findAllCmsFieldCached() {
        return cacheableTemplateProvider.findAllFields();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "global", key = "'allCmsFields'"),
            @CacheEvict(value = "pebble", key = "'field_' + #id")
    })
    public void deleteCmsField(Long id) throws Exception {
        Optional<CmsFieldEntity> fieldset = cmsFieldRepository.findById(id);
        if (!fieldset.isPresent()) {
            throw new Exception("CmsField with id " + id + " is not found!");
        } else if (!fieldset.get().isDeletable()) {
            String message = "CmsField with name \" + name + \" is not deletable!";
            log.error(message);
            throw new Exception(message);
        }
        cmsFieldRepository.deleteById(id);
    }
}
