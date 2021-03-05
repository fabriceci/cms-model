package be.fcip.cms.persistence.service;

import be.fcip.cms.exception.EmailExistsException;
import be.fcip.cms.exception.IpLockedException;
import be.fcip.cms.persistence.model.PasswordResetTokenEntity;
import be.fcip.cms.persistence.model.UserEntity;
import be.fcip.cms.persistence.model.VerificationTokenEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Set;

public interface IUserService extends UserDetailsService {

    UserEntity save(UserEntity user);

    UserEntity saveWithGroups(UserEntity user, Set<String> groups);

    void delete(UserEntity user);

    void checkIpAttempts(String ip) throws IpLockedException;

    void checkUserAttempts(String username);

    //void resetFailAttempts(String username);

    void updateAttempts(String ip, String username, String pass);

    UserEntity registerNewUserAccount(UserEntity accountDto) throws EmailExistsException;

    UserEntity getUserByVerificationToken(String verificationToken);

    void createVerificationTokenForUser(UserEntity user, String token);

    VerificationTokenEntity getVerificationToken(String verificationToken);

    VerificationTokenEntity generateNewVerificationToken(String token);

    void createPasswordResetTokenForUser(UserEntity user, String token);

    UserEntity findByUsernameOrEmail(String email);

    PasswordResetTokenEntity getPasswordResetToken(String token);

    UserEntity getUserByPasswordResetToken(String token);

    UserEntity getUserByID(long id);

    void changeUserPassword(UserEntity user, String password);

    boolean checkIfValidOldPassword(UserEntity user, String password);

    String jsonAdminContent();

    UserEntity findById(Long id);
}
