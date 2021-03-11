package be.fcip.cms.persistence.service;

import be.fcip.cms.persistence.cache.ICacheablePageProvider;
import be.fcip.cms.persistence.model.PageFileEntity;
import be.fcip.cms.persistence.repository.IPageFileRepository;
import be.fcip.cms.util.FileExtensionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class FileServiceImpl implements IFileService {

    @Autowired
    private IPageFileRepository pageFileRepository;
    @Autowired
    private IPageService pageService;

    @Override
    public PageFileEntity findOne(Long id) {
        return pageFileRepository.findById(id).orElse(null);
    }

    @Override
    public PageFileEntity findServerName(String serverName) {
        return pageFileRepository.findByServerName(serverName);
    }


    @Override
    @Caching(evict = {
            @CacheEvict(value = "content", allEntries = true),

    })
    public PageFileEntity save(PageFileEntity file) {
        clearCache(file);
        return pageFileRepository.save(file);
    }

    public List<PageFileEntity> save(List<PageFileEntity> files) {
        for (PageFileEntity file : files) {
            clearCache(file);
        }
        return pageFileRepository.saveAll(files);
    }

    public void delete(Long id) {
        Optional<PageFileEntity> file = pageFileRepository.findById(id);
        if(file.isPresent()){
            clearCache(file.get());
            pageFileRepository.deleteById(id);
        }

    }

    private void clearCache(PageFileEntity file){
        if(file.getPageContent() == null) {
            log.error("File without content, id : " + file.getId());
        }
        long id = file.getPageContent().getId();
        if(id == 0) return;
        pageService.clearCache(id);;

    }

    @Override
    public List<PageFileEntity> getFilesList(Long contentDataId, String type) {
        List<PageFileEntity> files = null;
        if(type == null){
            files = pageFileRepository.findByPageContentIdOrderByPositionAsc(contentDataId);
        } else{
            files = pageFileRepository.findByPageContentIdAndFileTypeOrderByPositionAsc(contentDataId, type);
        }

        return files;
    }

    @Override
    public String getFilesListJson(Long contentDataId, String type) {
        List<PageFileEntity> files = getFilesList(contentDataId, type);

        JsonArrayBuilder data = Json.createArrayBuilder();
        JsonObjectBuilder row;

        for (PageFileEntity file : files) {
            row = Json.createObjectBuilder();
            //row.add("DT_RowId", "x"); // add an name
            //row.add("DT_RowClass", "x"); // add a class
            row.add("DT_RowData", Json.createObjectBuilder().add("id", file.getId()));
            row.add("name", StringUtils.trimToEmpty(file.getName()));
            row.add("description", StringUtils.trimToEmpty(file.getDescription()));
            row.add("group", StringUtils.trimToEmpty(file.getGroupName()));
            row.add("active", file.isEnabled());
            row.add("type", StringUtils.trimToEmpty(FileExtensionUtils.getFileImage(file.getExtension())));
            row.add("size", StringUtils.trimToEmpty(FileUtils.byteCountToDisplaySize(file.getSize())));
            data.add(row);
        }

        String result = Json.createObjectBuilder().add("data", data).build().toString();
        return result;
    }
}
