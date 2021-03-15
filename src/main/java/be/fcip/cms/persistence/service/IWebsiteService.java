package be.fcip.cms.persistence.service;

import be.fcip.cms.persistence.model.WebsiteEntity;

import java.util.List;

public interface IWebsiteService {

    WebsiteEntity save(WebsiteEntity website);
    List<WebsiteEntity> findAll();
    void delete(Long id);
}
