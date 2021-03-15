package be.fcip.cms.persistence.service;

import be.fcip.cms.persistence.model.WebsiteEntity;
import be.fcip.cms.persistence.repository.IWebsiteRepository;
import be.fcip.cms.util.ApplicationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service(value = "websiteService")
@Transactional
public class WebsiteServiceImpl implements IWebsiteService{

    @Autowired
    IWebsiteRepository websiteRepository;

    @Override
    public WebsiteEntity save(WebsiteEntity website) {

        WebsiteEntity save = websiteRepository.save(website);
        updateApplicationUtils();
        return save;

    }

    @Override
    public List<WebsiteEntity> findAll() {
        return websiteRepository.findAll();
    }

    @Override
    public Optional<WebsiteEntity> findById(Long id) {
        return websiteRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        websiteRepository.deleteById(id);
        updateApplicationUtils();
    }

    private void updateApplicationUtils(){
        ApplicationUtils.websites = findAll().stream().collect(Collectors.toMap(WebsiteEntity::getId, w -> w));
    }
}
