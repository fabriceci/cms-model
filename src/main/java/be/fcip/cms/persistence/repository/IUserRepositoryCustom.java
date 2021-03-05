package be.fcip.cms.persistence.repository;

import be.fcip.cms.persistence.model.UserEntity;

public interface IUserRepositoryCustom {

    UserEntity findUserCustom(String email);
}
