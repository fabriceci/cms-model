package be.fcip.cms.persistence.cache;

import be.fcip.cms.persistence.model.TaxonomyEntity;
import be.fcip.cms.persistence.repository.ITaxonomyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CacheableTaxonomyProviderImpl implements ICacheableTaxonomyProvider{

    @Autowired private ITaxonomyRepository taxonomyTermRepository;

    @Override
    @Cacheable(value = "taxonomy")
    public List<TaxonomyEntity> findByType(String type) {
        return Collections.unmodifiableList(taxonomyTermRepository.findAllByTaxonomyTypeNameOrderByPositionAscNameAsc(type));
    }

    @Override
    @Cacheable(value = "taxonomy")
    public List<TaxonomyEntity> findAllTerm() {
        return Collections.unmodifiableList(taxonomyTermRepository.findAll());
    }

    @Override
    @Cacheable(value = "taxonomy")
    public List<String> findAllType() {
        return Collections.unmodifiableList(taxonomyTermRepository.findAllType());
    }
}
