package be.fcip.cms.pebble.view;

import be.fcip.cms.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.LocaleUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
    Render a dynamic field in CMS Page administration.
 */
@Component
@Slf4j
public class GeneralHelper {

    public String languageName(String lang){
        return new Locale(lang).getDisplayLanguage();
    }

    /*

    public Locale getLocale(String code){
        Locale locale = LocaleUtils.toLocale(code);
        return ApplicationUtils.locales.contains(locale) ? locale : null;
    }

    private static Pattern extractYoutubeId = Pattern.compile("v=(\\w+)");

    public static String extractYtId(String url){
        Matcher match = extractYoutubeId.matcher(url);
        if(match.find()){
            return match.group(1);
        }
        return null;
    }

    public static Long currentAssetsNumber(){
        return CmsUtils.CMS_UNIQUE_NUMBER;
    }
    public static boolean isJSONValid(String json) {
        return CmsJsonUtils.isJSONValid(json);
    }

    public static String toJson(Object o){
        return CmsJsonUtils.toJson(o);
    }

    public static String getFullUrl(HttpServletRequest request){
        return CmsHttpUtils.getFullURL(request);
    }




    public static String getYearStringFromDate(Date date){
        return CmsDateUtils.getYearStringFromDate(date);
    }

    public List<Long> getLoopList(Long from, Long to){
        return CmsNumericUtils.getLoopList(from, to);
    }
    */
}
