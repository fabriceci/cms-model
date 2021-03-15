package be.fcip.cms.util;

import be.fcip.cms.persistence.model.GroupEntity;
import be.fcip.cms.persistence.model.PermissionEntity;
import be.fcip.cms.persistence.model.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.csrf.CsrfToken;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class CmsSecurityUtils {

    public static UserEntity getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserEntity custom = null;
        if (auth != null && !(auth instanceof AnonymousAuthenticationToken)) {
            try {
                custom = (UserEntity) auth.getPrincipal();
            } catch (Exception e) {
                log.error(e.toString());
            }
        }
        return custom;
    }

    public static boolean hasGroup(String role) {
        UserEntity user = getCurrentUser();
        if (user == null)
            return false;

        for (GroupEntity group : user.getGroups()) {
            if (group.getName().equals(role))
                return true;
        }
        return false;
    }

    public static boolean isSuperAdmin() {
        return hasGroup(CmsUtils.GROUP_SUPER_ADMIN);
    }

    public static boolean hasGroup(UserEntity user, String role) {
        if (user == null)
            return false;

        for (GroupEntity group : user.getGroups()) {
            if (group.getName().equals(role))
                return true;
        }
        return false;
    }

    public static boolean isLogged(){
        return getCurrentUser() != null;
    }

    public static boolean hasRole(String role) {
        // get security context from thread local
        UserEntity currentUser = getCurrentUser();
        return currentUser != null && currentUser.getAuthorities().contains(new SimpleGrantedAuthority(role));

    }

    public static boolean hasAnyRole(Collection<String> roles) {
        UserEntity currentUser = getCurrentUser();

        if(currentUser != null) {
            for (String role : roles) {
                if(currentUser.getAuthorities().contains(new SimpleGrantedAuthority(role)))
                    return true;
            }
        }
        return false;
    }

    private static Collection<SimpleGrantedAuthority> getSimpleGrantedAuthorityList(Collection<String> roles){
        Collection<SimpleGrantedAuthority> result = new ArrayList<>();
        for (String role : roles) {
            result.add(new SimpleGrantedAuthority(role));
        }
        return result;
    }

    public static boolean hasRolesStr(Collection<String> roles) {
        UserEntity currentUser = getCurrentUser();
        return currentUser != null && currentUser.getAuthorities().containsAll(getSimpleGrantedAuthorityList(roles));
    }


    public static boolean hasRoles(Collection<PermissionEntity> roles) {
        if(roles == null)
            return false;
        return hasRolesStr(roles.stream().map(n -> n.getName()).collect(Collectors.toList()));
    }

    public static UserEntity getPrincipal(SessionRegistry sessionRegistry, UserDetails user) {
        for (Object principal : sessionRegistry.getAllPrincipals()) {
            final UserEntity loggedUser = (UserEntity) principal;
            if (loggedUser.getEmail().equals(user.getUsername()))
                return loggedUser;
        }
        return null;
    }

    public static boolean userIsLogged(SessionRegistry sessionRegistry, UserEntity user){
        return getPrincipal(sessionRegistry, user) != null;
    }

    public static void updateSessionUser(UserDetails user){
        Authentication newAuth = new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    public static void expireSession(SessionRegistry sessionRegistry, UserDetails user){
        UserEntity principal = getPrincipal(sessionRegistry, user);
        if(principal != null) {
            List<SessionInformation> allSessions = sessionRegistry.getAllSessions(principal, false);
            for (SessionInformation session : allSessions) {
                session.expireNow();
            }
        }
    }

    public static boolean refererIsAdmin(HttpServletRequest request){
        String referer = request.getHeader("referer");
        String baseUrl = CmsHttpUtils.getBaseUrl(request);
        if(StringUtils.isEmpty(referer) || !referer.startsWith(baseUrl)) return false;

        String requestURI = referer.replace(baseUrl, "");
        return requestURI.startsWith("/admin/") || requestURI.equals("/admin");
    }

    public static String getCsrfInput(HttpServletRequest request) {
        Object param = request.getAttribute("_csrf");
        CsrfToken csrf = (param instanceof CsrfToken ? (CsrfToken) param : null);
        String csrfName = csrf != null ? csrf.getParameterName() : "";
        String csrfValue = csrf != null ? csrf.getToken() : "";
        return "<input type=\"hidden\" name=\"" + csrfName + "\" value=\"" + csrfValue + "\" />";
    }

    public static boolean uriIsAdmin(HttpServletRequest request){
        String requestURI = request.getRequestURI();
        return requestURI.startsWith("/admin/") || requestURI.equals("/admin");
    }
}
