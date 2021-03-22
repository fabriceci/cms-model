package be.fcip.cms.pebble.view;

import be.fcip.cms.util.CmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class AssetsHelper {

    public static final String  ASSETS_PATH = "/public/assets/" ;

    public String get(String path){
        return ASSETS_PATH + path + "?" + CmsUtils.CMS_UNIQUE_NUMBER;
    }

    public static String thumbnailPath(String path){
        if(StringUtils.isEmpty(path)) return null;

        final String fullPath = FilenameUtils.getFullPath(path);
        final String name = FilenameUtils.getBaseName(path);
        final String ext = FilenameUtils.getExtension(path);
        return fullPath + name + "_thumb" + "." + ext;
    }
}
