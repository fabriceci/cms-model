package be.fcip.cms.persistence.service;

import be.fcip.cms.persistence.model.BlockEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface IBlockService {

    BlockEntity find(Long id);
    BlockEntity findCached(Long id);

    List<BlockEntity> findAll();

    String jsonBlockArray(String type, boolean canDelete);

    // save & delete
    @PreAuthorize("hasRole('ROLE_ADMIN_BLOCK_DELETE')")
    void delete(Long id) throws Exception;

    @PreAuthorize("hasRole('ROLE_ADMIN_BLOCK')")
    BlockEntity save(BlockEntity block);

    @PreAuthorize("hasRole('ROLE_ADMIN_BLOCK')")
    List<BlockEntity> save(List<BlockEntity> blocks);
}
