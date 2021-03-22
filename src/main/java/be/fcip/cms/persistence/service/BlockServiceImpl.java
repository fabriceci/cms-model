package be.fcip.cms.persistence.service;

import be.fcip.cms.persistence.cache.ICacheableBlockProvider;
import be.fcip.cms.persistence.model.BlockEntity;
import be.fcip.cms.persistence.repository.IBlockRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class BlockServiceImpl implements IBlockService {

    @Autowired private IBlockRepository blockRepository;
    @Autowired private ICacheableBlockProvider cacheableBlockProvider;

    @Override
    public BlockEntity find(Long id) {
        return blockRepository.findById(id).orElse(null);
    }

    @Override
    public BlockEntity findCached(Long id) {
        return cacheableBlockProvider.find(id);
    }

    @Override
    public List<BlockEntity> findAll() {
        return blockRepository.findAll();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "block", key = "#id"),
            @CacheEvict(value = "pebble", key = "'block_' + #id"),
            @CacheEvict(value= "pageFull", allEntries = true),
            @CacheEvict(value = "pageShort", allEntries = true)
    })
    public void delete(Long id) throws Exception {
        Optional<BlockEntity> block = blockRepository.findById(id);
        if (!block.isPresent()) {
            throw new Exception("Block with id " + id + " is not found!");
        } else if (!block.get().isDeletable()) {
            String message = "Block with id " + id + " is not deletable!";
            log.error(message);
            throw new Exception(message);
        }
        blockRepository.deleteById(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "block", key = "#block.id"),
            @CacheEvict(value = "pebble", key = "'block_' + #block.id"),
            @CacheEvict(value= "pageFull", allEntries = true),
            @CacheEvict(value = "pageShort", allEntries = true)
    })
    public BlockEntity save(BlockEntity block) {
        return blockRepository.save(block);
    }


    @Override
    public List<BlockEntity> save(List<BlockEntity> blocks) {

        for (BlockEntity block : blocks) {
            save(block);
        }
        return blocks;
    }

    @Override
    public String jsonBlockArray(String type, boolean canDelete) {
        List<BlockEntity> blocks = null;
        if (type == null || type.equals("all")) {
            blocks = blockRepository.findAll();
        } else {
            blocks = blockRepository.findAllByTypeAndDynamic(type, false);
        }
        JsonArrayBuilder data = Json.createArrayBuilder();
        JsonObjectBuilder row;
        // reload tree like this : table.ajax.reload()
        for (BlockEntity block : blocks) {
            row = Json.createObjectBuilder();
            row.add("DT_RowData", Json.createObjectBuilder().add("id", block.getId()));
            row.add("name", StringUtils.trimToEmpty(block.getName()));
            row.add("type", StringUtils.trimToEmpty(block.getType()));
            row.add("dynamic", block.isDynamic());
            row.add("deletable", block.isDeletable() && canDelete);
            data.add(row);
        }
        return Json.createObjectBuilder().add("data", data).build().toString();
    }
}