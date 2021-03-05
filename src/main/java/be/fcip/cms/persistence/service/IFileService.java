package be.fcip.cms.persistence.service;

import be.fcip.cms.persistence.model.PageFileEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface IFileService {

    PageFileEntity findOne(Long id);

    PageFileEntity findServerName(String serverName);

    @PreAuthorize("hasRole('ROLE_ADMIN_FILE')")
    PageFileEntity save(PageFileEntity file);

    @PreAuthorize("hasRole('ROLE_ADMIN_FILE')")
    List<PageFileEntity> save(List<PageFileEntity> files);

    @PreAuthorize("hasRole('ROLE_ADMIN_FILE_DELETE')")
    void delete(Long id);

    String getFilesListJson(Long contentDataId, String type);

    List<PageFileEntity> getFilesList(Long contentDataId, String type);
}
