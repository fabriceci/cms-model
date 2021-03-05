package be.fcip.cms.persistence.cache;

import be.fcip.cms.persistence.model.CmsFieldEntity;
import be.fcip.cms.persistence.model.PageTemplateEntity;
import be.fcip.cms.persistence.repository.ICmsFieldRepository;
import be.fcip.cms.persistence.repository.IPageTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CacheableTemplateProviderImpl implements ICacheableTemplateProvider {

    @Autowired
    private IPageTemplateRepository contentTemplateRepository;
    @Autowired
    private ICmsFieldRepository cmsFieldRepository;

    @Override
    @Cacheable(value = "template", key = "#id")
    public PageTemplateEntity find(Long id) {
        return contentTemplateRepository.findByIdWithFieldset(id);
    }

    @Override
    @Cacheable(value = "template", key = "'allCmsFields'")
    public List<CmsFieldEntity> findAllFields() {
        return cmsFieldRepository.findAll();
    }
}
