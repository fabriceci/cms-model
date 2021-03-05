package be.fcip.cms.model.module;

import java.util.Map;

public interface ICmsModule {

    String getModuleName();

    String render(final Map<String, ?> model) throws ModuleRenderingException;

}

