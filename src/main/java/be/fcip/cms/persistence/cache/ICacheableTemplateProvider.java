package be.fcip.cms.persistence.cache;

import be.fcip.cms.persistence.model.CmsFieldEntity;
import be.fcip.cms.persistence.model.PageTemplateEntity;

import java.util.List;

public interface ICacheableTemplateProvider {

    PageTemplateEntity find(Long id);

    List<CmsFieldEntity> findAllFields();
}
