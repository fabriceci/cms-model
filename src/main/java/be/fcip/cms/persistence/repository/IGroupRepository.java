package be.fcip.cms.persistence.repository;

import be.fcip.cms.persistence.model.GroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IGroupRepository extends JpaRepository<GroupEntity, Long>, QuerydslPredicateExecutor<GroupEntity> {
    GroupEntity findByName(String name);

    @Query("SELECT g from GroupEntity g  WHERE g.name <> :group ")
    List<GroupEntity> findAllForClient(@Param("group") String group);
}
