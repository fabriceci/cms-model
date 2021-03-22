package be.fcip.cms.service;

import be.fcip.cms.persistence.model.BlockEntity;
import be.fcip.cms.persistence.service.IBlockService;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class PebbleServiceCacheProviderImpl implements IPebbleServiceCacheProvider{

    @Autowired
    private IBlockService blockService;
    @Autowired
    @Qualifier("pebbleStringEngine")
    private PebbleEngine pebbleStringEngine;

    @Override
    @Cacheable(value = "block", key = "'compiled_block_' + #id")
    public PebbleTemplate getCompiledTemplate(Long id) throws PebbleException {

        BlockEntity block = blockService.findWithCache(id);
        return pebbleStringEngine.getTemplate(block.getContent(), "block_" + block.getId(), true);
    }

}
