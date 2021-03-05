package be.fcip.cms.persistence.repository;

import be.fcip.cms.persistence.model.QGroupEntity;
import be.fcip.cms.persistence.model.QUserEntity;
import be.fcip.cms.persistence.model.UserEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class IUserRepositoryImpl implements IUserRepositoryCustom {

    @PersistenceContext(unitName = "core")
    private EntityManager entityManager;

    @Override
    public UserEntity findUserCustom(String email) {
        JPAQueryFactory queryFactor = new JPAQueryFactory(entityManager);
        QUserEntity qUserEntity = QUserEntity.userEntity;
        QGroupEntity qGroupEntity = QGroupEntity.groupEntity;
        return queryFactor.selectFrom(qUserEntity)
                .leftJoin(qUserEntity.groups, qGroupEntity).fetchJoin()
                .leftJoin(qGroupEntity.permissions).fetchJoin()
                .where(qUserEntity.email.eq(email)).fetchOne();
    }
}
