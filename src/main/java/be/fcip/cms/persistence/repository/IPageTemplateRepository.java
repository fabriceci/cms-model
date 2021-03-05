package be.fcip.cms.persistence.repository;

import be.fcip.cms.persistence.model.PageTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IPageTemplateRepository extends JpaRepository<PageTemplateEntity, Long>, QuerydslPredicateExecutor<PageTemplateEntity> {

    @Query("SELECT c from PageTemplateEntity c LEFT JOIN FETCH c.block WHERE c.id = :id ")
    PageTemplateEntity findByIdWithFieldset(@Param("id") Long id);

    List<PageTemplateEntity> findByTypeLike(String name);

    @Query("SELECT c from PageTemplateEntity c LEFT JOIN FETCH c.block WHERE c.name = :name ")
    PageTemplateEntity findFirstByName(@Param("name") String name);

    @Query("SELECT c from PageTemplateEntity c  WHERE c.block.id = :id ")
    List<PageTemplateEntity> findByBlockId(@Param("id") Long id);


}
