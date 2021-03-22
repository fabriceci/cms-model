package be.fcip.cms.persistence.cache;

import be.fcip.cms.persistence.model.BlockEntity;

public interface ICacheableBlockProvider {
    BlockEntity find(Long id);
}
