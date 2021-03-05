package be.fcip.cms.persistence.cache;

import be.fcip.cms.persistence.model.BlockEntity;
import be.fcip.cms.persistence.repository.IBlockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CacheableBlockProviderImpl implements ICacheableBlockProvider {

    @Autowired
    private IBlockRepository blockRepository;

    @Override
    @Cacheable(value = "block", key = "#id")
    public BlockEntity find(Long id) {
        return blockRepository.findById(id).orElse(null);
    }

    @Override
    @Cacheable(value = "block", key = "#name")
    public BlockEntity findByName(String name) {
        return blockRepository.findByName(name);
    }
}
