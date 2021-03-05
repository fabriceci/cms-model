package be.fcip.cms.persistence.envers;

import be.fcip.cms.persistence.model.UserEntity;
import be.fcip.cms.util.CmsSecurityUtils;
import org.hibernate.envers.RevisionListener;

public class UserAndDateRevisionListener implements RevisionListener {

    @Override
    public void newRevision(Object revisionEntity) {
        RevInfo entity = (RevInfo) revisionEntity;

        //entity.setModifiedDate(new Date());
        UserEntity userDto = CmsSecurityUtils.getCurrentUser();
        if(userDto != null) {
            entity.setUsername(userDto.getEmail());
            entity.setUserId(userDto.getId());
        }
    }
}

