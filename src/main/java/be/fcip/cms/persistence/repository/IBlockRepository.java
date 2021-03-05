package be.fcip.cms.persistence.repository;

import be.fcip.cms.persistence.model.BlockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface IBlockRepository extends JpaRepository<BlockEntity, Long>, QuerydslPredicateExecutor<BlockEntity> {
    List<BlockEntity> findAllByType(String type);
    List<BlockEntity> findAllByTypeAndDynamic(String type, boolean dynamic);
    BlockEntity findByName(String name);
}
