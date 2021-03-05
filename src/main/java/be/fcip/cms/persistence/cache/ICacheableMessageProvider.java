package be.fcip.cms.persistence.cache;

import java.util.Map;

public interface ICacheableMessageProvider {

    Map<String, Map<String, String>> mapOfTranslation();
}
