package be.fcip.cms.persistence.repository;

import be.fcip.cms.persistence.model.UserEntity;
import be.fcip.cms.persistence.model.VerificationTokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

public interface IVerificationTokenRepository extends JpaRepository<VerificationTokenEntity, Long>, QuerydslPredicateExecutor<VerificationTokenEntity> {

    VerificationTokenEntity findByToken(String token);

    VerificationTokenEntity findByUser(UserEntity user);
}