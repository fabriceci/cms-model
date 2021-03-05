package be.fcip.cms.persistence.repository;

import be.fcip.cms.persistence.model.WordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;
import java.util.Set;

public interface IWordRepository extends JpaRepository<WordEntity, Long>, QuerydslPredicateExecutor<WordEntity> {


    WordEntity findByWordKey(String key);

    List<WordEntity> findByDomainNotIn(List<String> types);

    @Query("SELECT DISTINCT m.domain from WordEntity m")
    List<String> findDomainList();

    @Query("SELECT m from WordEntity  m LEFT JOIN FETCH m.translations")
    Set<WordEntity> findAllMessage();
}
