package be.fcip.cms.persistence.service;

import be.fcip.cms.persistence.model.CmsFieldEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface ICmsFieldService {

    CmsFieldEntity findCmsField(Long id);

    String jsonCmsField();
    List<CmsFieldEntity> findAllCmsFieldCached();
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    void deleteCmsField(Long id) throws Exception;
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    CmsFieldEntity saveCmsField(CmsFieldEntity fieldset);
}
