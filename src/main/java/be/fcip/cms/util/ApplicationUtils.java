package be.fcip.cms.util;

import be.fcip.cms.persistence.model.WebsiteEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpRequest;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.GrantedAuthority;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ApplicationUtils {

    public static Properties prop;
    public static Locale defaultLocale;
    public static Locale defaultAdminLocale;
    public final static Set<Locale> locales;
    public static Set<Locale> adminLocales;
    public static boolean isDev;
    public static boolean forceLangInUrl;
    public static boolean publicMember;

    public static List<GrantedAuthority> rolesList;
    public static Map<Long, WebsiteEntity> websites;

    static{
        // Load Properties
        InputStream is = null;
        prop = new Properties();
        try {
            // load core app.properties
            is = ApplicationUtils.class.getResourceAsStream("/application.properties");
            prop.load(is);
            if (is != null) { is.close(); }
            // load app app.properties
            is = ApplicationUtils.class.getResourceAsStream("/config/application.properties");
            prop.load(is);
            if (is != null) { is.close(); }
            // check override
            Path path = Paths.get("config/application.properties");
            if(Files.exists(path)){
                is = Files.newInputStream(path);
                prop.load(is);
                if (is != null) { is.close(); }
            }
        }
        catch (IOException e) { e.printStackTrace(); }

        locales = Arrays.stream(prop.getProperty("cms.lang.list").split(",")).map(LocaleUtils::toLocale).collect(Collectors.toSet());
        adminLocales = Arrays.stream(prop.getProperty("cms.lang.admin.list").split(",")).map(LocaleUtils::toLocale).collect(Collectors.toSet());
        defaultLocale = LocaleUtils.toLocale(prop.getProperty("cms.lang.default"));
        defaultAdminLocale = LocaleUtils.toLocale(prop.getProperty("cms.lang.default.admin"));
        forceLangInUrl = Boolean.parseBoolean(prop.getProperty("cms.lang.url"));
        isDev = "dev".equals(prop.getProperty("spring.profiles.active"));
        publicMember = Boolean.parseBoolean(prop.getProperty("cms.public.member"));
    }

    public static Locale getLocale(){
        return getLocale(LocaleContextHolder.getLocale());
    }

    public static Locale getLocale(Locale locale){
        // HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        // if(request != null && request.getRequestURI().startsWith("/admin")){
        //    return adminLocales.contains(locale) ? locale : defaultAdminLocale;
        //}
        return locales.contains(locale) ? locale : defaultLocale;
    }

    public static Locale getLocale(String strLocale){
        if(StringUtils.isEmpty(strLocale)) return defaultLocale;
        Locale locale = LocaleUtils.toLocale(strLocale);
        return locales.contains(locale) ? locale : defaultLocale;
    }

    public static Locale getAdminLocale(){
        Locale locale = LocaleContextHolder.getLocale();
        return adminLocales.contains(locale) ? locale : defaultAdminLocale;
    }

    public static WebsiteEntity getWebsiteFromUrl(HttpServletRequest request){
        return getWebsiteFromUrl(request.getRequestURI());
    }

    public static WebsiteEntity getWebsiteFromUrl(String path){
        int cpt = 0;
        for (WebsiteEntity website : ApplicationUtils.websites.values()) {
            if (cpt == 0) {
                cpt++; // skip first
                continue;
            }
            if(ApplicationUtils.forceLangInUrl){
                for (Locale siteLocale : ApplicationUtils.locales) {
                    if (path.startsWith("/" + siteLocale + website.getSlug())) {
                        return website;
                    }
                }

            } else {
                if (path.startsWith(website.getSlug())) {
                    return website;
                }
            }

        }
        return websites.get(1L);
    }
}
