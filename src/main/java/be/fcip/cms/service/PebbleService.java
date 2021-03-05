package be.fcip.cms.service;

import be.fcip.cms.hook.IModelExtension;
import be.fcip.cms.persistence.model.BlockEntity;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.ExtensionRegistry;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service("peebleService")
@Slf4j
public class PebbleService implements IPeebleService {

    @Autowired
    @Qualifier("pebbleStringEngine")
    private PebbleEngine pebbleStringEngine;
    @Autowired
    private List<IModelExtension> modelExtensionList;
    @Autowired
    private IPebbleServiceCacheProvider cacheProvider;

    @Override
    public String parseString(String data, Map<String, Object> model)  throws IOException, PebbleException{
        if (model == null) {
            model = new LinkedHashMap<>(10);
        }
        ExtensionRegistry extensionRegistry = pebbleStringEngine.getExtensionRegistry();
        PebbleTemplate template = pebbleStringEngine.getTemplate(data, null, true);
        Writer writer = new StringWriter();
        template.evaluate(writer, model, LocaleContextHolder.getLocale());
        return writer.toString();
    }

    @Override
    public String parseBlock(BlockEntity block, Map<String, Object> model) throws IOException, PebbleException {
        if(block == null) throw new IllegalArgumentException();
        if (model == null) {
            model = new HashMap<>(10);
        }
        PebbleTemplate compiledTemplate = cacheProvider.getCompiledTemplate(block.getId());
        Writer writer = new StringWriter();
        compiledTemplate.evaluate(writer, model, LocaleContextHolder.getLocale());
        return writer.toString();
    }


    @Override
    public void fillModelMap(Map<String, Object> model, HttpServletRequest request) {

        modelExtensionList.sort((o1, o2) -> o2.getPriority() - o1.getPriority());

        boolean isAdmin = request.getRequestURI().startsWith("/admin/");
        for (IModelExtension modelExtension : modelExtensionList) {
            if(modelExtension.excludeAdmin() && isAdmin) continue;
            modelExtension.fillModelMap(model, request);
        }
    }
}
