package be.fcip.cms.persistence.cache;

import be.fcip.cms.persistence.model.WordEntity;
import be.fcip.cms.persistence.model.WordTranslationEntity;
import be.fcip.cms.persistence.repository.IWordRepository;
import be.fcip.cms.util.ApplicationUtils;
import org.apache.commons.lang3.LocaleUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class CacheableMessageProviderImpl implements ICacheableMessageProvider {

    @Autowired
    private IWordRepository messageRepository;

    @Override
    @Cacheable(value = "localizedMessage")
    public Map<String, Map<String, String>> mapOfTranslation() {
        Set<WordEntity> messages = messageRepository.findAllMessage();
        Map<String, Map<String, String>> result = new HashMap<>();
        for (WordEntity message : messages) {
            Map<String, String> mapLang = new HashMap<>();

            for (Map.Entry<String, WordTranslationEntity> entry : message.getTranslations().entrySet()) {
                WordTranslationEntity t = entry.getValue();
                if (ApplicationUtils.locales.contains(LocaleUtils.toLocale(t.getLanguage()))
                    || ApplicationUtils.adminLocales.contains(LocaleUtils.toLocale(t.getLanguage()))
                ) {
                    // replace ' by '', because message formatter need this
                    mapLang.put(t.getLanguage(), t.getValue().replace("\'", "''"));
                }
            }

            result.put(message.getWordKey(), mapLang);
        }
        return result;
    }
}
