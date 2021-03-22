package be.fcip.cms.util;

import be.fcip.cms.persistence.model.PermissionEntity;
import be.fcip.cms.persistence.repository.IPermissionRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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

    public synchronized static void refreshPermission(IPermissionRepository repository){
        // load all privilege
        final List<GrantedAuthority> authorities = new ArrayList<>();
        for (PermissionEntity roleEntity : repository.findAll()) {
            authorities.add(new SimpleGrantedAuthority(roleEntity.getName()));
        }
        rolesList = Collections.synchronizedList(authorities);
    }
}
