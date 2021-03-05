package be.fcip.cms.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Slf4j
public class CmsAssetsUtils {

    private final static String CSS_MODEL_BEGIN = "<link rel=\"stylesheet\" type=\"text/css\" href=\"";
    private final static String CSS_MODEL_END = "\">";
    private final static String JS_MODEL_BEGIN = "<script src=\"";
    private final static String JS_MODEL_END = "\"></script>";
    private final static String ASSETS_PATH = "/public/assets/";

    public static String  getAssets(String name){
        if(StringUtils.isEmpty(name)) return null;
        return doWork(name);
    }

    public static String getAssets(List<String> list) {

        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(doWork(s)).append('\n');
        }
        return sb.toString();
    }

    private static String doWork(final String name){
        if(name.endsWith("js")){
            return getJs(name);
        } else if(name.endsWith("css")){
            return getCss(name);
        } else{
            return getAssetPath(name);
        }
    }

    public static String getJs(final String name){
        if(name == null) return null;
        return JS_MODEL_BEGIN + ASSETS_PATH + name + "?" + CmsUtils.CMS_UNIQUE_NUMBER + JS_MODEL_END;
    }

    public static String getCss(final String name){
        if(name == null) return null;
        return CSS_MODEL_BEGIN + ASSETS_PATH + name + "?" + CmsUtils.CMS_UNIQUE_NUMBER + CSS_MODEL_END;
    }

    public static String getAssetPath(final String name){
        if(name == null) return null;
        return WebConfigConstants.RESOURCES_LOCATION + name;
    }
}
