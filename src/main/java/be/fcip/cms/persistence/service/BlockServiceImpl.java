package be.fcip.cms.persistence.service;

import be.fcip.cms.persistence.cache.ICacheableBlockProvider;
import be.fcip.cms.persistence.model.BlockEntity;
import be.fcip.cms.persistence.model.PageTemplateEntity;
import be.fcip.cms.persistence.repository.IAuditRepository;
import be.fcip.cms.persistence.repository.IBlockRepository;
import be.fcip.cms.persistence.repository.IPageTemplateRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
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

    @Autowired
    private IAuditRepository auditRepository;
    @Autowired
    private IBlockRepository blockRepository;
    @Autowired
    private IPageTemplateRepository pageTemplateRepository;
    @Autowired
    private ICacheableBlockProvider cacheableBlockProvider;
    @Autowired
    private CacheManager cacheManager;

    @Override
    public BlockEntity find(Long id) {
        return cacheableBlockProvider.find(id);
    }

    @Override
    public List<BlockEntity> findAll() {
        return blockRepository.findAll();
    }

    @Override
    public BlockEntity findByName(String name) {
        return cacheableBlockProvider.findByName(name);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "block", key = "#id"),
            @CacheEvict(value = "block", key = "'compiled_block_' + #id")
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
        cleanCache(block.get());
        blockRepository.deleteById(id);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "block", key = "#block.id"),
            @CacheEvict(value = "block", key = "'compiled_block_' + #block.id")
    })
    public BlockEntity save(BlockEntity block) {
        cleanCache(block);
        return blockRepository.save(block);
    }


    @Override
    public List<BlockEntity> save(List<BlockEntity> blocks) {

        for (BlockEntity block : blocks) {
            save(block);
        }
        return blocks;
    }

    private void cleanCache(BlockEntity block){
        if(block.getId() != 0){
            List<PageTemplateEntity> byBlockId = pageTemplateRepository.findByBlockId(block.getId());
            // Suppress templates
            Cache cacheTemplate = cacheManager.getCache("template");
            for (PageTemplateEntity template : byBlockId) {
                cacheTemplate.evict(template.getId());
            }
            // suppress block by name
            Cache cacheBlock = cacheManager.getCache("block");
            cacheBlock.evict(block.getName());
        }

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

    @Override
    public List<Number> getRevisionNumberList(Long id) {
        return auditRepository.getRevisionNumberList(BlockEntity.class, id);
    }

    @Override
    public BlockEntity getRevisionEntity(Number id){
        try {
            BlockEntity singleResult = (BlockEntity) auditRepository.getRevisionEntity(BlockEntity.class, id);
            return singleResult;
        } catch(Exception e){
            log.error("Revision Cast Exception, ID : " + id, e);
            return null;
        }
    }

    @Override
    public Object[] getRevision(Number id) {
        return auditRepository.getRevision(BlockEntity.class, id);
    }
}