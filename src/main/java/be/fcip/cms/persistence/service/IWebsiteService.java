package be.fcip.cms.persistence.service;

import be.fcip.cms.persistence.model.WebsiteEntity;

import java.util.List;
import java.util.Optional;

public interface IWebsiteService {

    WebsiteEntity save(WebsiteEntity website);
    List<WebsiteEntity> findAll();
    Optional<WebsiteEntity> findById(Long id);
    void delete(Long id);
}
