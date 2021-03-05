package be.fcip.cms.persistence.repository;

import be.fcip.cms.persistence.model.AppParamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface IAppParamRepository extends JpaRepository<AppParamEntity, String>, QuerydslPredicateExecutor<AppParamEntity> {
}
