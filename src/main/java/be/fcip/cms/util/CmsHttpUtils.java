package be.fcip.cms.util;

import com.google.gson.internal.LinkedHashTreeMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CmsHttpUtils {

    public static boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    public static String getRemoteIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public static String getFullURL(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }

    public static String getBaseUrl(HttpServletRequest request) {
        return request.getRequestURL().substring(0, request.getRequestURL().length() - request.getRequestURI().length()) + request.getContextPath();
    }


    public static Map<String, Object> getAttributes(HttpServletRequest request) {
        Map<String, Object> attr = new HashMap<>();
        Enumeration params = request.getAttributeNames();
        if (params != null) {
            while (params.hasMoreElements()) {
                String paramName = (String) params.nextElement();
                attr.put(paramName, request.getAttribute(paramName));
            }
        }
        return attr;
    }

    public static Map<String, Object> getParameters(HttpServletRequest request) {

        Map<String, Object> get = new HashMap<>();
        Enumeration params = request.getParameterNames();
        if (params != null) {
            while (params.hasMoreElements()) {
                String paramName = (String) params.nextElement();
                get.put(paramName, request.getParameter(paramName));
            }
        }
        return get;
    }

    public static Map<String, String> queryStringToMap(String queryString){
        Map<String, String> queryMap = new LinkedHashTreeMap<>();
        try {
            for (String pair : queryString.split("&")) {
                int eq = pair.indexOf("=");
                if (eq < 0) {
                    // key with no value
                    queryMap.put(URLDecoder.decode(pair, "UTF-8"), "");
                }
                else {
                    // key=value
                    String key = URLDecoder.decode(pair.substring(0, eq), "UTF-8");
                    String value = URLDecoder.decode(pair.substring(eq + 1), "UTF-8");
                    queryMap.put(key, value);
                }
            }
        } catch(UnsupportedEncodingException e) {
            log.error("URLDecoder", e);
        }
        return queryMap;
    }

    public static Map<String, String> unencodedQueryStringToMap(String queryString){
        Map<String, String> queryMap = new LinkedHashTreeMap<>();

        for (String pair : queryString.split("&")) {
            int eq = pair.indexOf("=");
            if (eq < 0) {
                // key with no value
                queryMap.put(pair, "");
            }
            else {
                // key=value
                String key = pair.substring(0, eq);
                String value = pair.substring(eq + 1);
                queryMap.put(key, value);
            }
        }
        return queryMap;
    }

    public static String mapToQueryString(Map<String,String> map){
        StringBuilder sb = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                if (sb.length() > 0) {
                    sb.append('&');
                }
                sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                if (!StringUtils.isEmpty(entry.getValue())) {
                    sb.append('=');
                    sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
                }
            }
        } catch(UnsupportedEncodingException e) {
            log.error("URLDecoder", e);
        }
        return sb.toString();
    }

    public static String encodeParam(String value) {

        String encoded = null;
        try{
            encoded =  URLEncoder.encode(value, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException silent){
            encoded = "";
        }

        return encoded;
    }

    public static String encodeUrl(String url) throws NotImplementedException {
        if(url == null) return null;

        int pos = url.indexOf("?");
        if(pos==-1) return url;
        String base = url.substring(0,pos);
        String query = url.substring(pos+1);
        return base + '?' + mapToQueryString(unencodedQueryStringToMap(query));
    }

    public static String decodeParam(String value) {
        String decoded = null;
        try {
            decoded = URLDecoder.decode(value, StandardCharsets.UTF_8.toString());
        } catch(UnsupportedEncodingException e){
            decoded ="";
        }
        return decoded;
    }

    public static String getQueryString(String url){
        if(url == null) throw new IllegalArgumentException();
        if(StringUtils.countMatches(url, '?') > 1 ) throw new IllegalArgumentException();

        int i = url.indexOf('?');
        if(i == -1) return null;

        return url.substring(i+1);
    }

}
