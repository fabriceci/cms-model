package be.fcip.cms.persistence.cache;

import java.util.Map;

public interface ICacheableAppParamsProvider {

    Map<String, String> getParams();
}
