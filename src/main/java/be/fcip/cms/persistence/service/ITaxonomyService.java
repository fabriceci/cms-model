package be.fcip.cms.persistence.service;

import be.fcip.cms.persistence.model.TaxonomyEntity;

import java.util.List;

public interface ITaxonomyService {

    void deleteTerm(String term, String type);
    void deleteTerm(Long id);
    //TaxonomyEntity saveTerm(TaxonomyEntity termEntity);
    TaxonomyEntity findTermEntity(Long id);
    TaxonomyEntity findByType(String term, String type);
    TaxonomyEntity createIfNotExist(String term, String type);
    TaxonomyEntity add(String term, String type);
    List<TaxonomyEntity> createIfNotExist(List<String> terms, String type);
    List<TaxonomyEntity> findByTypeCached(String type);
    String findByTypeJson(String type);
    List<TaxonomyEntity> findAllCached();
    List<String> findAllTypeCached();
    String getJson();
}
