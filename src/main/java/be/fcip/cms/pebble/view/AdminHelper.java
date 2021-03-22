package be.fcip.cms.pebble.view;

import be.fcip.cms.hook.IAdminMenu;
import be.fcip.cms.persistence.model.AdminMenuItem;
import be.fcip.cms.util.CmsSecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component(value = "adminHelper")
@Slf4j
public class AdminHelper {

    @Autowired private List<IAdminMenu> items;

    public List<AdminMenuItem> menu(){
        List<AdminMenuItem> result = new ArrayList<>();

        for (IAdminMenu item : items) {
            for (AdminMenuItem adminMenuItem : item.getItems()) {

                List<String> roles = adminMenuItem.getRoles();
                if(roles != null){
                    // check if the currentUser is allowed
                    if(!CmsSecurityUtils.hasRolesStr(roles)){
                        continue;
                    };
                }
                result.add(adminMenuItem);
            }
        }

        Collections.sort(result);
        return result;
    }
}
