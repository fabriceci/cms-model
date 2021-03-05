package be.fcip.cms.persistence.repository;

import be.fcip.cms.persistence.model.PasswordResetTokenEntity;
import be.fcip.cms.persistence.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface IPasswordResetTokenRepository extends JpaRepository<PasswordResetTokenEntity, Long>, QuerydslPredicateExecutor<PasswordResetTokenEntity> {

    PasswordResetTokenEntity findByToken(String token);

    PasswordResetTokenEntity findByUser(UserEntity user);
}