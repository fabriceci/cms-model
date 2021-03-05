package be.fcip.cms.persistence.service;

import be.fcip.cms.exception.EmailExistsException;
import be.fcip.cms.exception.IpLockedException;
import be.fcip.cms.persistence.cache.ICacheableUserProvider;
import be.fcip.cms.persistence.model.GroupEntity;
import be.fcip.cms.persistence.model.PasswordResetTokenEntity;
import be.fcip.cms.persistence.model.UserEntity;
import be.fcip.cms.persistence.model.VerificationTokenEntity;
import be.fcip.cms.persistence.repository.IPasswordResetTokenRepository;
import be.fcip.cms.persistence.repository.IUserRepository;
import be.fcip.cms.persistence.repository.IVerificationTokenRepository;
import be.fcip.cms.util.CmsDateUtils;
import be.fcip.cms.util.CmsSecurityUtils;
import be.fcip.cms.util.CmsUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.time.LocalDateTime;
import java.util.*;

@Service("userService")
@Transactional
public class UserServiceImpl implements IUserService {

    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IAuthorityService authorityService;
    @Autowired
    private CacheManager cacheManager;

    //@Value("${max.ip.attemps}")
    //private int maxIpAttempts;
    //@Value("${max.login.attemps}")
    //private int maxLoginAttempts;
    @Autowired
    @Qualifier("passwordEncoder")
    private PasswordEncoder passwordEncoder;
    @Autowired
    private IVerificationTokenRepository verificationTokenRepository;
    @Autowired
    private SessionRegistry sessionRegistry;
    @Autowired
    private IPasswordResetTokenRepository passwordResetTokenRepository;
    @Autowired
    private ICacheableUserProvider cacheableUserProvider;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = cacheableUserProvider.findByUsernameOrEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username not found");
        }

        return user;
    }

    @Override
    public UserEntity findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public UserEntity findByUsernameOrEmail(String email) {
        return cacheableUserProvider.findByUsernameOrEmail(email);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "user", key = "#user.email"),
    })
    public UserEntity save(UserEntity user) {
        Optional<UserEntity> old = userRepository.findById(user.getId());
        if(old.isPresent()){
            UserEntity oldUser = old.get();
            // case email change
            if(!oldUser.getEmail().equals(user.getEmail())){
                Cache userCache = cacheManager.getCache("user");
                userCache.evict(oldUser.getEmail());
            }
        }
        UserEntity editedUser = userRepository.save(user);
        if(CmsSecurityUtils.userIsLogged(sessionRegistry, editedUser)){
            CmsSecurityUtils.updateSessionUser(editedUser);
        }
        return editedUser;
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "user", key = "#user.email"),
    })
    public UserEntity saveWithGroups(UserEntity user, Set<String> groupsString) {

        if(groupsString == null || groupsString.isEmpty()){
            user.getGroups().clear();
        } else {
            Set<GroupEntity> groups = new HashSet<>();
            for (String r : groupsString) {
                if(r.equals(CmsUtils.GROUP_SUPER_ADMIN)){
                    if(!CmsSecurityUtils.isSuperAdmin()){
                        continue;
                    }
                }
                GroupEntity roleEntity = authorityService.findGroupByName(r);
                groups.add(roleEntity);
            }
            user.setGroups(groups);
        }
        return save(user);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "user", key = "#user.email"),
    })
    public void delete(UserEntity user) {

        UserEntity userDto = cacheableUserProvider.findByUsernameOrEmail(user.getEmail());
        CmsSecurityUtils.expireSession(sessionRegistry, userDto);
        userRepository.delete(user);
    }

    @Override
    @Transactional(noRollbackFor = {LockedException.class})
    public void checkUserAttempts(String username) {

        UserEntity user = findByUsernameOrEmail(username);

        if (user != null) {

            LocalDateTime dateTime = LocalDateTime.now();
            dateTime = dateTime.minusDays(7L);
            Date maxDate = CmsDateUtils.LocalDateTimeToDate(dateTime);
            /*
            Long tentative = attemptsRespository.countByUsernameAndDateAfter(user.getEmail(), maxDate);

            if (tentative >= maxLoginAttempts) {

                userRepository.updateAccountLocked(false, user.getEmail());
                throw new LockedException("User is disabled");
            }

         */
        }
    }

    @Override
    @Transactional(noRollbackFor = {LockedException.class})
    public void checkIpAttempts(String ip) throws IpLockedException {

        LocalDateTime dateTime = LocalDateTime.now();
        dateTime = dateTime.minusHours(1L);
        Date maxDate = CmsDateUtils.LocalDateTimeToDate(dateTime);
        /*
        Long tentative = attemptsRespository.countByIpAndDateAfter(ip, maxDate);
        if(tentative >= (maxIpAttempts)){
            throw new IpLockedException(maxDate.toString());
        }

         */

    }

    @Override
    public void updateAttempts(String ip, String username, String pass) {
      //  attemptsRespository.save(new AttemptsEntity(0, username, ip, pass, new Date()));
    }

    @Override
    public UserEntity registerNewUserAccount(UserEntity accountDto) throws EmailExistsException {
        if (emailExist(accountDto.getEmail())) {
            throw new EmailExistsException("There is an account with that email address: " + accountDto.getEmail());
        }
        final UserEntity user = new UserEntity();
        user.setFirstName(accountDto.getFirstName());
        user.setLastName(accountDto.getLastName());
        user.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        user.setEmail(accountDto.getEmail());
        return userRepository.save(user);
    }

    @Override
    public UserEntity getUserByVerificationToken(String verificationToken) {

        return verificationTokenRepository.findByToken(verificationToken).getUser();
    }

    @Override
    public VerificationTokenEntity getVerificationToken(String verificationToken) {

        return verificationTokenRepository.findByToken(verificationToken);
    }


    @Override
    public void createVerificationTokenForUser(UserEntity user, String token) {
        final VerificationTokenEntity myToken = new VerificationTokenEntity(token, user);
        verificationTokenRepository.save(myToken);
    }

    @Override
    public VerificationTokenEntity generateNewVerificationToken(String token) {
        VerificationTokenEntity vToken = verificationTokenRepository.findByToken(token);
        vToken.updateToken(UUID.randomUUID().toString());
        vToken = verificationTokenRepository.save(vToken);
        return vToken;
    }

    @Override
    public void createPasswordResetTokenForUser(UserEntity user, String token) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(user.getId());
        final PasswordResetTokenEntity myToken = new PasswordResetTokenEntity(token, userEntity);
        passwordResetTokenRepository.save(myToken);
    }

    @Override
    public PasswordResetTokenEntity getPasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    @Override
    public UserEntity getUserByPasswordResetToken(String token) {

        return passwordResetTokenRepository.findByToken(token).getUser();
    }

    @Override
    public UserEntity getUserByID(long id) {

        return userRepository.findById(id).orElse(null);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "user", key = "#userDto.email"),
    })
    public void changeUserPassword(UserEntity userDto, String password) {
        final UserEntity user = userRepository.findById(userDto.getId()).orElse(null);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    @Override
    public boolean checkIfValidOldPassword(UserEntity user, String password) {

        return passwordEncoder.matches(password, user.getPassword());
    }

    private boolean isUserExists(String username) {

        boolean result = false;

        long count = userRepository.countByEmail(username);
        if (count > 0) {
            result = true;
        }

        return result;
    }

    private boolean emailExist(final String email) {
        final UserEntity user = userRepository.findByEmail(email);
        if (user != null) {
            return true;
        }
        return false;
    }

    @Override
    public String jsonAdminContent() {

        List<UserEntity> userEntityList = userRepository.findByOrderByEnabledDescLastNameAsc();
        JsonArrayBuilder data = Json.createArrayBuilder();
        JsonObjectBuilder row;

        for (UserEntity u : userEntityList) {
            row = Json.createObjectBuilder();
            row.add("DT_RowData", Json.createObjectBuilder().add("id", u.getId()));
            row.add("name", StringUtils.trimToEmpty(u.getLastName()));
            row.add("firstname", StringUtils.trimToEmpty(u.getFirstName()));
            row.add("email",  StringUtils.trimToEmpty(u.getEmail()));
            row.add("active", u.isEnabled());
            row.add("locked", !u.isAccountNonLocked());
            StringBuilder rolesBuilder = new StringBuilder();
            /*final Collection<? extends GrantedAuthority> authorities = u.getAuthorities();

            for (GrantedAuthority authority : authorities) {
                if (roles.length() != 0) {
                    roles.append(" ,");
                }
                roles.append(authority.getAuthority().replace("_PRIVILEGE", ""));
            }*/
            Collection<GroupEntity> roles = u.getGroups();
            for (GroupEntity role : roles) {
                if (rolesBuilder.length() != 0) {
                    rolesBuilder.append(" ,");
                }
                rolesBuilder.append(role.getName().replace("ROLE_", ""));

            }
            row.add("role", StringUtils.trimToEmpty(rolesBuilder.toString()));



            data.add(row);
        }

        return Json.createObjectBuilder().add("data", data).build().toString();
    }
}