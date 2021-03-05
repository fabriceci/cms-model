package be.fcip.cms.persistence.repository;

import be.fcip.cms.persistence.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface IUserRepository extends JpaRepository<UserEntity, Long>, QuerydslPredicateExecutor<UserEntity>, IUserRepositoryCustom {

    List<UserEntity> findAllByEnabledIsFalse();

    List<UserEntity> findAllByAccountNonLockedIsFalse();

    List<UserEntity> findByOrderByEnabledDescLastNameAsc();

    List<UserEntity> findByEnabledTrueAndAccountNonExpiredTrueAndAccountNonLockedTrueOrderByLastName();

    Long countByEmail(String username);

    @Modifying
    @Query("update UserEntity u set u.accountNonLocked = :isNonLocked where u.email = :username")
    void updateAccountLocked(@Param("isNonLocked") boolean isNonLocked, @Param("username") String username);

    UserEntity findByEmail(String email);

    @Modifying
    @Query("delete from UserEntity u where u.email != :email")
    void deleteAllNonadmin(@Param("email") String email);
}
