package be.fcip.cms.pebble.view;

import be.fcip.cms.util.CmsHttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;

@Component
@Slf4j
public class HttpHelper {


    public static boolean isAjax(HttpServletRequest request) {
        return CmsHttpUtils.isAjax(request);
    }

    public static String getBaseUrl(HttpServletRequest request) {
        return CmsHttpUtils.getBaseUrl(request);
    }
    public static String getFullURL (HttpServletRequest request) {
        return CmsHttpUtils.getFullURL(request);
    }

    public static String encodeParam(String param) throws UnsupportedEncodingException {
        return CmsHttpUtils.encodeParam(param);
    }

    public static String encodeUrl(String url){
        return CmsHttpUtils.encodeUrl(url);
    }
}
