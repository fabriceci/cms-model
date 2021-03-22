package be.fcip.cms.persistence.service;

import be.fcip.cms.persistence.model.WordEntity;

import java.util.List;
import java.util.Map;

public interface IMessageService {

    List<WordEntity> findAll();

    List<WordEntity> findAllPublic();

    List<String> findAllDomain();

    WordEntity save(WordEntity message);

    List<WordEntity> save(List<WordEntity> messages);

    void delete(Long id);

    Map<String, Map<String, String>> mapOfTranslationCached();

    WordEntity findByMessageKey(String key);

    WordEntity find(Long id);

    WordEntity addMessage(Long id, List<String> langs, List<String> values, String domain);

    WordEntity addMessage(WordEntity wordEntity);
    List<WordEntity> addMessages(List<WordEntity> wordEntities);
}
