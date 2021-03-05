package be.fcip.cms.pebble.view;

import be.fcip.cms.persistence.model.PageEntity;
import be.fcip.cms.persistence.service.IPageService;
import be.fcip.cms.util.CmsSecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@Slf4j
public class SecurityHelper {
    @Autowired
    private IPageService contentService;

    public boolean hasRole(String role){
        return CmsSecurityUtils.hasRole(role);
    }

    public boolean isSuperAdmin(){
        return CmsSecurityUtils.isSuperAdmin();
    }

    public boolean hasRoles(Collection<String> roles){
        return CmsSecurityUtils.hasRolesStr(roles);
    }

    public boolean hasAnyRole(Collection<String> roles){
        return CmsSecurityUtils.hasAnyRole(roles);
    }

    public static boolean isLogged(){
        return CmsSecurityUtils.isLogged();
    }

    public void checkRole(PageEntity content){
        boolean result = userHasRole(content);
        result = false;
        if(!result){
            throw new AccessDeniedException("You don't have permission to access the resource");
        }
    }

    public boolean userHasRole(PageEntity content){
        return CmsSecurityUtils.hasRoles(contentService.getRoleForContent(content));
    }


}
