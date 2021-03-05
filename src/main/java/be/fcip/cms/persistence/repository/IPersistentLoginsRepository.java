package be.fcip.cms.persistence.repository;

import be.fcip.cms.persistence.model.PersistentLoginsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface IPersistentLoginsRepository extends JpaRepository<PersistentLoginsEntity, Long>, QuerydslPredicateExecutor<PersistentLoginsEntity> {
    PersistentLoginsEntity findOneBySeries(String series);

    Long deleteByUsername(String username);
}