package be.fcip.cms.persistence.cache;

import be.fcip.cms.persistence.model.CmsFieldEntity;
import be.fcip.cms.persistence.model.PageTemplateEntity;
import be.fcip.cms.persistence.repository.ICmsFieldRepository;
import be.fcip.cms.persistence.repository.IPageTemplateRepository;
import de.cronn.reflection.util.immutable.ImmutableProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CacheableTemplateProviderImpl implements ICacheableTemplateProvider {

    @Autowired private IPageTemplateRepository contentTemplateRepository;
    @Autowired private ICmsFieldRepository cmsFieldRepository;

    @Override
    @Cacheable(value = "template", key = "#id")
    public PageTemplateEntity find(Long id) {
        return ImmutableProxy.create(contentTemplateRepository.findById(id).orElse(null));
    }

    @Override
    @Cacheable(value = "global", key = "'allCmsFields'")
    public List<CmsFieldEntity> findAllFields() {
        return Collections.unmodifiableList(cmsFieldRepository.findAll());
    }
}
