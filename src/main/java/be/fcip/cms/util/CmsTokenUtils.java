package be.fcip.cms.util;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CmsTokenUtils {

    public static Pattern PROCESS_REGEX_PATTERN = Pattern.compile("\\[([a-zA-Z_-]+)\\]");

    /**
     * Replace the token [xxx] by the param xxx in the map
     * @param template
     * @param lang
     * @return
     */
    public static String parse(String template, Map<String, String> map){
        Matcher matcher = PROCESS_REGEX_PATTERN.matcher(template);
        int count = 0;
        StringBuffer sb = null;
        while(matcher.find()) {
            count++;
            if(sb == null) sb = new StringBuffer();
            String key = (String)matcher.group(1);
            if(map.containsKey(key)){
                matcher.appendReplacement(sb, map.get(key));
            }
        }
        if(count == 0){
            return template;
        } else {
            matcher.appendTail(sb);
            return sb.toString();
        }
    }
}
