package be.fcip.cms.persistence.service;

import be.fcip.cms.persistence.model.PageFileEntity;
import be.fcip.cms.persistence.repository.IPageFileRepository;
import be.fcip.cms.util.FileExtensionUtils;
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

@Service
@Transactional
public class FileServiceImpl implements IFileService {

    @Autowired
    private IPageFileRepository pageFileRepository;

    @Override
    @Caching(evict = {
            @CacheEvict(value = "content", allEntries = true),

    })
    public PageFileEntity save(PageFileEntity file) {
        return pageFileRepository.save(file);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "content", allEntries = true),

    })
    public List<PageFileEntity> save(List<PageFileEntity> files) {
        return pageFileRepository.saveAll(files);
    }

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
    public void delete(Long id) {
        pageFileRepository.deleteById(id);
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
