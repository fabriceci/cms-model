package be.fcip.cms.persistence.service;

import be.fcip.cms.persistence.model.PageFileEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface IFileService {

    PageFileEntity findOne(Long id);

    @PreAuthorize("hasRole('ROLE_ADMIN_FILE')")
    PageFileEntity save(PageFileEntity file);

    @PreAuthorize("hasRole('ROLE_ADMIN_FILE')")
    List<PageFileEntity> save(List<PageFileEntity> files);

    public void delete(Long id);

    PageFileEntity findServerName(String serverName);

    String getFilesListJson(Long contentDataId, String type);

    List<PageFileEntity> getFilesList(Long contentDataId, String type);
}
