package be.fcip.cms.hook;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface IModelExtension {

    int getPriority();

    default boolean excludeAdmin() { return true; }

    void fillModelMap(Map<String, Object> model, HttpServletRequest request);

}
