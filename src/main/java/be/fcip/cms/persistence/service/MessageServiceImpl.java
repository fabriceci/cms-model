package be.fcip.cms.persistence.service;

import be.fcip.cms.persistence.cache.ICacheableMessageProvider;
import be.fcip.cms.persistence.model.QWordEntity;
import be.fcip.cms.persistence.model.QWordTranslationEntity;
import be.fcip.cms.persistence.model.WordEntity;
import be.fcip.cms.persistence.model.WordTranslationEntity;
import be.fcip.cms.persistence.repository.IWordRepository;
import be.fcip.cms.util.ApplicationUtils;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.jpa.QueryHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class MessageServiceImpl implements IMessageService {

    QWordEntity wordEntity = QWordEntity.wordEntity;
    QWordTranslationEntity translationEntity = QWordTranslationEntity.wordTranslationEntity;

    @Autowired
    private IWordRepository messageRepository;

    @Autowired
    private ICacheableMessageProvider cacheableMessageProvider;

    @PersistenceContext(unitName = "core")
    private EntityManager entityManager;

    private static List<String> adminDomainTypes;
    {
        adminDomainTypes = new ArrayList<>();
        adminDomainTypes.add("admin");
        adminDomainTypes.add("error");
        adminDomainTypes.add("general");
    }

    @Override
    @CacheEvict(value = "global", key="'messages'")
    public WordEntity save(WordEntity message) {
        return messageRepository.save(message);
    }

    @Override
    @CacheEvict(value = "global", key="'messages'")
    public List<WordEntity> save(List<WordEntity> messages) {
        return messageRepository.saveAll(messages);
    }

    @Override
    @CacheEvict(value = "global", key="'messages'")
    public void delete(Long id) {
        messageRepository.deleteById(id);
    }

    @Override
    public List<WordEntity> findAll() {
        return new JPAQueryFactory(entityManager)
                .selectFrom(wordEntity)
                .leftJoin(wordEntity.translations, translationEntity).fetchJoin()
                .distinct()
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH,false)
                .fetch();
    }

    @Override
    public List<WordEntity> findAllPublic() {
        return new JPAQueryFactory(entityManager)
                .selectFrom(wordEntity)
                .leftJoin(wordEntity.translations, translationEntity).fetchJoin()
                .where(wordEntity.domain.notIn(adminDomainTypes))
                .distinct()
                .setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH,false)
                .fetch();
    }

    @Override
    public List<String> findAllDomain() {
        return messageRepository.findDomainList();
    }

    @Override
    public Map<String, Map<String, String>> mapOfTranslation() {
        return cacheableMessageProvider.mapOfTranslation();
    }

    @Override
    public WordEntity findByMessageKey(String key) {
        return messageRepository.findByWordKey(key);
    }

    @Override
    public WordEntity find(Long id) {
        return messageRepository.findById(id).orElse(null);
    }

    @Override
    @CacheEvict(value = "global", key="'messages'")
    public WordEntity addMessage(Long id, List<String> langs, List<String> values, String domain) {

        WordEntity messageEntity = messageRepository.findById(id).orElse(null);
        Map<String, WordTranslationEntity> translations = messageEntity.getTranslations();

        for (int i = 0; i < langs.size() ; i++) {
            if (ApplicationUtils.locales.contains(LocaleUtils.toLocale(langs.get(i)))
                    || ApplicationUtils.adminLocales.contains(LocaleUtils.toLocale(langs.get(i)))
            ){
                WordTranslationEntity messageTranslationsEntity = translations.get(langs.get(i));
                if(!StringUtils.isEmpty(values.get(i)) && !values.get(i).trim().equals("/")) {
                    if (messageTranslationsEntity != null) {
                        messageTranslationsEntity.setValue(values.get(i));
                    } else {
                        messageTranslationsEntity = new WordTranslationEntity();
                        messageTranslationsEntity.setLanguage(langs.get(i));
                        messageTranslationsEntity.setWord(messageEntity);
                        messageTranslationsEntity.setValue(values.get(i));
                        translations.put(langs.get(i), messageTranslationsEntity);
                    }
                }
            }
        }

        messageEntity.setTranslations(translations);
        messageEntity.setDomain(domain);

        return messageRepository.save(messageEntity);
    }

    @Override
    public WordEntity addMessage(WordEntity wordEntity) {
        return messageRepository.save(wordEntity);
    }

    @Override
    public List<WordEntity> addMessages(List<WordEntity> wordEntities) {
        return messageRepository.saveAll(wordEntities);
    }
}
