package be.fcip.cms.hook;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public interface IPeebleValidationModelExtension {
    void fillModelMap(Map<String, Object> model, HttpServletRequest request);
}
