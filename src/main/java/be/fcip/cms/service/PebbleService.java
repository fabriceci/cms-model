package be.fcip.cms.service;

import be.fcip.cms.hook.IModelExtension;
import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.extension.ExtensionRegistry;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service("peebleService")
@Slf4j
public class PebbleService implements IPeebleService {

    @Autowired @Qualifier("pebbleStringEngine") private PebbleEngine pebbleStringEngine;
    @Autowired private List<IModelExtension> modelExtensionList;
    @Autowired private IPebbleServiceCacheProvider cacheProvider;

    @PostConstruct
    public void init(){
        modelExtensionList.sort((o1, o2) -> o2.getPriority() - o1.getPriority());
    }

    @Override
    public String parseString(String data, Map<String, Object> model, String cacheKey)  throws IOException, PebbleException{
        if(data == null) throw new RuntimeException("Parsed String could not be null");
        if (model == null) {
            model = new LinkedHashMap<>(10);
        }
        ExtensionRegistry extensionRegistry = pebbleStringEngine.getExtensionRegistry();
        PebbleTemplate template = null;
        if(StringUtils.isEmpty(cacheKey)){
            template = pebbleStringEngine.getTemplate(data);
        } else {
            template = cacheProvider.getCompiledTemplate(data, cacheKey);
        }
        Writer writer = new StringWriter();
        template.evaluate(writer, model, LocaleContextHolder.getLocale());
        return writer.toString();
    }

    @Override
    public void fillModelMap(Map<String, Object> model, HttpServletRequest request) {

        boolean isAdmin = request.getRequestURI().startsWith("/admin/");
        for (IModelExtension modelExtension : modelExtensionList) {
            if(modelExtension.excludeAdmin() && isAdmin) continue;
            if(!modelExtension.supportedMethods().contains( request.getMethod())) continue;
            modelExtension.fillModelMap(model, request);
        }
    }
}
