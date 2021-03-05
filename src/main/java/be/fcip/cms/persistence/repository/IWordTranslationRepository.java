package be.fcip.cms.persistence.repository;

import be.fcip.cms.persistence.model.WordTranslationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface IWordTranslationRepository extends JpaRepository<WordTranslationEntity, Long>, QuerydslPredicateExecutor<WordTranslationEntity> {
}
