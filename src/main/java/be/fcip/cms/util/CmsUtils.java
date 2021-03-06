package be.fcip.cms.util;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.cache.CacheManager;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
public class CmsUtils {

    //public final String csrfParameterName = "_csrf";
    public final static String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public final static String DATE_FORMAT = "yyyy-MM-dd";

    public final static String CONTENT_TYPE_PAGE = "PAGE";

    public final static String FIELD_TYPE_STRING = "string";
    public final static String FIELD_TYPE_DATE = "date";

    public final static String HEADER_VALIDATION_FAILED = "Validation-Failed";

    public final static String GROUP_SUPER_ADMIN = "GROUP_SUPER_ADMIN";

    public final static String ROLE_MEMBER = "ROLE_MEMBER";
    public final static String ROLE_ADMIN = "ROLE_ADMIN";
    public final static String ROLE_ADMIN_SEO = "ROLE_ADMIN_SEO";
    public final static String ROLE_SUPER_ADMIN = "ROLE_SUPER_ADMIN";
    public final static String ROLE_ADMIN_CMS = "ROLE_ADMIN_CMS";
    public final static String ROLE_ADMIN_CMS_FILE = "ROLE_ADMIN_CMS_FILE";
    public final static String ROLE_ADMIN_CMS_DELETE = "ROLE_ADMIN_CMS_DELETE";
    public final static String ROLE_ADMIN_WEBCONTENT = "ROLE_ADMIN_WEBCONTENT";
    public final static String ROLE_ADMIN_WEBCONTENT_DELETE = "ROLE_ADMIN_WEBCONTENT_DELETE";
    public final static String ROLE_ADMIN_USER = "ROLE_ADMIN_USER";
    public final static String ROLE_ADMIN_USER_DELETE = "ROLE_ADMIN_USER_DELETE";
    public final static String ROLE_ADMIN_BLOCK = "ROLE_ADMIN_BLOCK";
    public final static String ROLE_ADMIN_BLOCK_DELETE = "ROLE_ADMIN_BLOCK_DELETE";
    public final static String ROLE_ADMIN_GROUP = "ROLE_ADMIN_GROUP";
    public final static String ROLE_ADMIN_GROUP_DELETE = "ROLE_ADMIN_GROUP_DELETE";

    public final static long TEMPLATE_FOLDER_ID = 3;
    public final static long TEMPLATE_LINK_ID = 4;

    public static Long CMS_UNIQUE_NUMBER = new Date().getTime();
    public static Long CMS_MINIFY_NUMBER = null;

    public static String alert(@NonNull String type, @NonNull String message, String title) {
        StringBuilder sb = new StringBuilder();
        sb.append("<div class=\"alert alert-").append(type).append("\" role=\"alert\">");
        if (title != null) {
            sb.append("<strong>").append(title).append("</strong> - ");
        }
        sb.append(message);
        sb.append("</div>");
        return sb.toString();
    }

    public static String getResourceFileContent(String resourceName) throws IOException, URISyntaxException {
        InputStream resourceStream = CmsUtils.class.getResourceAsStream(resourceName);
        if(resourceStream == null) throw new RuntimeException("ressource not found: " + resourceName);
        String result = IOUtils.toString(resourceStream, StandardCharsets.UTF_8.name());
        resourceStream.close();
        return result;
    }

    public static void clearCaches(CacheManager cacheManager){
        for (String cacheName : cacheManager.getCacheNames()) {
            cacheManager.getCache(cacheName).clear();
        }
    }
}
