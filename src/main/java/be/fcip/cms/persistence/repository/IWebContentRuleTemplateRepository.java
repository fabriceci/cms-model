package be.fcip.cms.persistence.repository;

import be.fcip.cms.persistence.model.WebContentRuleTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface IWebContentRuleTemplateRepository extends JpaRepository<WebContentRuleTemplateEntity, String>, QuerydslPredicateExecutor<WebContentRuleTemplateEntity> {
}
