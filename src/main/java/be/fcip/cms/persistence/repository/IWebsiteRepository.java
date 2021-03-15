package be.fcip.cms.persistence.repository;

import be.fcip.cms.persistence.model.WebsiteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface IWebsiteRepository extends JpaRepository<WebsiteEntity, Long>, QuerydslPredicateExecutor<WebsiteEntity> {
}
