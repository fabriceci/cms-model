package be.fcip.cms.persistence.repository;

import be.fcip.cms.persistence.model.PageTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IPageTemplateRepository extends JpaRepository<PageTemplateEntity, Long>, QuerydslPredicateExecutor<PageTemplateEntity> {

    List<PageTemplateEntity> findByTypeLike(String name);

    @Query("SELECT c from PageTemplateEntity c WHERE c.name = :name ")
    PageTemplateEntity findFirstByName(@Param("name") String name);
}
