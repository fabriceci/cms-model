package be.fcip.cms.persistence.repository;

import be.fcip.cms.persistence.model.PageContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface IPageContentRepository extends JpaRepository<PageContentEntity, Long>, QuerydslPredicateExecutor<PageContentEntity> {

    Optional<PageContentEntity> findBySlug(String slug);

    @Query("SELECT c.page.id from PageContentEntity c WHERE c.enabled = true AND c.computedSlug = :slug AND c.language = :lang")
    Long findContentIdByComputedSlugAndLanguageLocale(@Param("slug") String slug, @Param("lang")String lang);

}
