package be.fcip.cms.persistence.repository;

import be.fcip.cms.persistence.model.PageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface IPageRepository extends JpaRepository<PageEntity, Long>, QuerydslPredicateExecutor<PageEntity>, IPageRepositoryCustom {

    @Query("SELECT p from PageEntity p LEFT JOIN FETCH p.pageChildren LEFT JOIN FETCH p.pageParent WHERE p.pageType like 'PAGE%' AND p.website.id = :websiteId ORDER BY p.position")
    Set<PageEntity> findAllPages(@Param("websiteId") Long websiteId);

    @Query("SELECT p from PageEntity p WHERE p.template.id = :id")
    Set<PageEntity> findAllPagesByTemplate(@Param("id") Long id);

    @Query("SELECT p from PageEntity p LEFT JOIN FETCH p.template WHERE p.template.dynamicUrl = true")
    Set<PageEntity> findAllDynamicUrlPages();

    @Query("SELECT DISTINCT p from PageEntity p LEFT JOIN FETCH p.pageChildren WHERE p.pageType like 'PAGE%' and p.pageParent IS NULL and p.website.id = :websiteId ORDER BY p.position")
    List<PageEntity> findByPageParentIsNullOrderAndWebsiteIdByPositionAsc(@Param("websiteId") Long websiteId);

    List<PageEntity> findByPageParentIdOrderByPositionAsc(long id);

    List<PageEntity> findAllByEnabledIsTrue();

    @Query("SELECT p from PageEntity p LEFT JOIN FETCH p.contentMap WHERE p.id = :id ")
    PageEntity findOne(@Param("id") Long id);


    // used before save an entity to get the good position
    PageEntity findFirstByPageParentOrderByPositionDesc(PageEntity parent);
    // used to set good position before a delete
    List<PageEntity> findByPageParentOrderByPositionAsc(PageEntity parent);
}
