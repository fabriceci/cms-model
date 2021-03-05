package be.fcip.cms.persistence.repository;

import be.fcip.cms.persistence.model.TaxonomyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ITaxonomyRepository extends JpaRepository<TaxonomyEntity, Long>, QuerydslPredicateExecutor<TaxonomyEntity> {

    @Query("SELECT DISTINCT m.type from TaxonomyEntity m ORDER BY m.type ASC")
    List<String> findAllType();

    @Query("SELECT DISTINCT t from TaxonomyEntity t WHERE t.name = :name and t.type = :type")
    TaxonomyEntity findByNameAndTaxonomyTypeName(@Param("name") String name,@Param("type") String type);

    @Query("SELECT DISTINCT t from TaxonomyEntity t WHERE t.type = :type ORDER BY t.position ASC, t.name ASC")
    List<TaxonomyEntity> findAllByTaxonomyTypeNameOrderByPositionAscNameAsc(@Param("type") String type);
}
