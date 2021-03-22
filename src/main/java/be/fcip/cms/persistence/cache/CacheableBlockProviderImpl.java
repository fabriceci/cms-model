package be.fcip.cms.persistence.cache;

import be.fcip.cms.persistence.model.BlockEntity;
import be.fcip.cms.persistence.repository.IBlockRepository;
import de.cronn.reflection.util.immutable.ImmutableProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CacheableBlockProviderImpl implements ICacheableBlockProvider {

    @Autowired
    private IBlockRepository blockRepository;

    @Override
    @Cacheable(value = "block", key = "#id")
    public BlockEntity find(Long id) {
        return ImmutableProxy.create(blockRepository.findById(id).orElse(null));
    }

    @Override
    @Cacheable(value = "block", key = "#name")
    public BlockEntity findByName(String name) {
        return ImmutableProxy.create(blockRepository.findByName(name));
    }
}
