package be.fcip.cms.persistence.service;

import be.fcip.cms.persistence.cache.ICacheableTaxonomyProvider;
import be.fcip.cms.persistence.model.PageEntity;
import be.fcip.cms.persistence.model.TaxonomyEntity;
import be.fcip.cms.persistence.model.WordEntity;
import be.fcip.cms.persistence.repository.ITaxonomyRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service(value = "taxonomyService")
@Transactional
public class TaxonomyServiceImpl implements ITaxonomyService {

    @Autowired private ITaxonomyRepository taxonomyTermRepository;
    @Autowired private IMessageService messageService;
    @Autowired private IPageService contentService;
    @Autowired private ICacheableTaxonomyProvider cacheableTaxonomyProvider;

    @Override
    public List<TaxonomyEntity> findAllCached() {
        return cacheableTaxonomyProvider.findAllTerm();
    }

    @Override
    public List<String> findAllTypeCached() {
        return cacheableTaxonomyProvider.findAllType();
    }

    @Override
    public List<TaxonomyEntity> findByTypeCached(String type) {
        return cacheableTaxonomyProvider.findByType(type);
    }

    @Override
    public TaxonomyEntity findByType(String term, String type) {
        return taxonomyTermRepository.findByNameAndTaxonomyTypeName(term.toLowerCase(), type);
    }

    @Override
    public String findByTypeJson(String type) {
        List<TaxonomyEntity> terms = findByTypeCached(type);
        JsonArrayBuilder data = Json.createArrayBuilder();
        for (TaxonomyEntity term : terms) {
            data.add(term.getName());
        }
        return data.build().toString();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "taxonomy", allEntries = true),
    })
    public void deleteTerm(String term, String type) {
        TaxonomyEntity termEntity = taxonomyTermRepository.findByNameAndTaxonomyTypeName(term.toLowerCase(), type);
        if(termEntity != null){
            taxonomyTermRepository.delete(termEntity);
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "taxonomy", allEntries = true),
    })
    public void deleteTerm(Long id) {
        TaxonomyEntity one = taxonomyTermRepository.findById(id).orElse(null);
        if(one != null){
            List<PageEntity> contents = one.getPages();
            for (PageEntity content : contents) {
                Set<TaxonomyEntity> taxonomyTermEntities = content.getTaxonomyEntities();
                taxonomyTermEntities.remove(one);
                content.setTaxonomyEntities(taxonomyTermEntities);
                contentService.savePage(content);

            }
            taxonomyTermRepository.deleteById(id);
        }

    }

    @Override
    public TaxonomyEntity findTermEntity(Long id) {
        return taxonomyTermRepository.findById(id).orElse(null);
    }


    @Override
    public TaxonomyEntity createIfNotExist(String term, String type) {
        TaxonomyEntity termEntity = taxonomyTermRepository.findByNameAndTaxonomyTypeName(term.toLowerCase(), type);
        if(termEntity == null){
            termEntity = add(term, type);
        }
        return termEntity;
    }

    @Override
    public List<TaxonomyEntity> createIfNotExist(List<String> terms, String type) {
        List<TaxonomyEntity> result = new ArrayList<>();
        for (String term : terms) {
            result.add(createIfNotExist(term, type));
        }
        return result;
    }

    @Override
    public TaxonomyEntity add(String term, String type){
        TaxonomyEntity termEntity = new TaxonomyEntity();
        termEntity.setName(term.toLowerCase());
        termEntity.setType(type);
        TaxonomyEntity newTerm = taxonomyTermRepository.save(termEntity);

        WordEntity messageEntity = messageService.findByMessageKey(term);
        if(messageEntity == null){
            messageEntity = new WordEntity();
            messageEntity.setDomain(type.toLowerCase());
            messageEntity.setWordKey(term);
            messageService.save(messageEntity);
        }

        return newTerm;
    }

    @Override
    public String getJson() {

        List<TaxonomyEntity> terms = taxonomyTermRepository.findAll();
        JsonArrayBuilder data = Json.createArrayBuilder();
        JsonObjectBuilder row;
        // reload tree like this : table.ajax.reload()
        for (TaxonomyEntity t : terms) {
            row = Json.createObjectBuilder();
            row.add("DT_RowData", Json.createObjectBuilder().add("id", t.getId()));
            row.add("name", StringUtils.trimToEmpty(t.getName()));
            row.add("type",  StringUtils.trimToEmpty(t.getType()));
            data.add(row);
        }

        return Json.createObjectBuilder().add("data", data).build().toString();
    }
}
