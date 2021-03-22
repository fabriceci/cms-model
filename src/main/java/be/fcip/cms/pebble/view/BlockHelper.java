package be.fcip.cms.pebble.view;

import be.fcip.cms.persistence.model.BlockEntity;
import be.fcip.cms.persistence.service.IBlockService;
import be.fcip.cms.service.IPeebleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class BlockHelper {

    @Autowired
    private IBlockService blockService;
    @Autowired
    private IPeebleService peebleService;

    public String getBlock(Long id) throws IOException{
        return getBlock(id, null);
    }

    public String getBlock(Long id, Map<String, Object> model) throws IOException {
        model = fillMap(model);
        BlockEntity blockEntity = blockService.findCached(id);
        if (blockEntity == null) return null;
        return blockEntity.isDynamic() ? peebleService.parseBlock(blockEntity, model) : blockEntity.getContent();
    }

    public String getBlockByName(String name) throws IOException {
        return getBlockByName(name, null);
    }

    public String getBlockByName(String name, Map<String, Object> model) throws IOException {
        model = fillMap(model);
        BlockEntity blockEntity = blockService.findByNameWithCache(name);
        if (blockEntity == null) return null;
        return blockEntity.isDynamic() ? peebleService.parseBlock(blockEntity, model) : blockEntity.getContent();
    }

    private Map<String, Object> fillMap(Map<String, Object> model){
        if(model == null){
            model = new HashMap();
        }
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        peebleService.fillModelMap(model, request);
        return model;
    }
}
