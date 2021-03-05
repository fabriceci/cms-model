package be.fcip.cms.persistence.repository;

import be.fcip.cms.persistence.model.PageFileEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.List;

public interface IPageFileRepository extends JpaRepository<PageFileEntity, Long>, QuerydslPredicateExecutor<PageFileEntity> {
    PageFileEntity findByServerName(String serverName);

    List<PageFileEntity> findByPageContentIdOrderByPositionAsc(Long id);

    List<PageFileEntity> findByPageContentIdAndFileTypeOrderByPositionAsc(Long id, String type);

}
