package be.fcip.cms.persistence.service;

import be.fcip.cms.persistence.model.PageTemplateEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface IPageTemplateService {

    List<PageTemplateEntity> findAllByTypeLike(String type);

    PageTemplateEntity findByName(String name);

    PageTemplateEntity findCached(Long id);

    PageTemplateEntity find(Long id);

    String jsonContent();

    // save & delete

    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    PageTemplateEntity save(PageTemplateEntity template);

    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    void delete(Long id) throws Exception;
}
