package be.fcip.cms.hook;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface IModelExtension {

    Set<String> DEFAULT_METHOD = new HashSet<>(Arrays.asList("GET"));
    int getPriority();

    default boolean excludeAdmin() { return true; }

    default Set<String> supportedMethods() { return DEFAULT_METHOD;  }

    void fillModelMap(Map<String, Object> model, HttpServletRequest request);

}
