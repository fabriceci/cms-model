package be.fcip.cms.persistence.cache;


import be.fcip.cms.persistence.model.UserEntity;

public interface ICacheableUserProvider {

    UserEntity findByUsernameOrEmail(String email);
}
