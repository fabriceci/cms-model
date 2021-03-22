package be.fcip.cms.persistence.cache;

import be.fcip.cms.persistence.model.UserEntity;
import be.fcip.cms.persistence.repository.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class CacheableUserProviderImpl implements ICacheableUserProvider {

    @Autowired private IUserRepository userRepository;

    /**
     * Note: Not immutable because spring security required this.
     * @param email
     * @return
     */
    @Override
    @Cacheable(value = "user", key = "#email")
    public UserEntity findByUsernameOrEmail(String email) {
        return userRepository.findUserCustom(email);
    }
}
