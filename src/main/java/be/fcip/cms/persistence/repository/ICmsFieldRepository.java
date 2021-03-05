package be.fcip.cms.persistence.repository;

import be.fcip.cms.persistence.model.CmsFieldEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface ICmsFieldRepository extends JpaRepository<CmsFieldEntity, Long>, QuerydslPredicateExecutor<CmsFieldEntity> {

}
