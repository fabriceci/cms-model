package be.fcip.cms.persistence.cache;

import be.fcip.cms.persistence.model.TaxonomyEntity;

import java.util.List;

public interface ICacheableTaxonomyProvider {

    List<TaxonomyEntity> findByType(String type);

    List<TaxonomyEntity> findAllTerm();

    List<String> findAllType();


}
