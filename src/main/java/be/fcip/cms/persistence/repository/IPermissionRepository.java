package be.fcip.cms.persistence.repository;

import be.fcip.cms.persistence.model.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface IPermissionRepository extends JpaRepository<PermissionEntity, Long>, QuerydslPredicateExecutor<PermissionEntity> {
    PermissionEntity findByName(String name);
}
