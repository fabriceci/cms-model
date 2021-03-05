package be.fcip.cms.persistence.model;

import be.fcip.cms.persistence.converter.UserGenderConverter;
import be.fcip.cms.util.CmsSecurityUtils;
import be.fcip.cms.util.CmsUtils;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "user")
@Getter
@Setter
@EqualsAndHashCode(of = {"id", "email", "password"}, callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity extends AbstractTimestampEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Access(AccessType.PROPERTY)
    @Column(nullable = false, updatable = false, columnDefinition = "SMALLINT(11) UNSIGNED")
    private long id;

    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    private String firstName;
    private String lastName;
    private String avatar;
    private String city;
    private String zip;
    private String street1;
    private String organisation;
    private LocalDate birthday;
    @Convert(converter = UserGenderConverter.class)
    @Column(length = 1)
    private Gender gender;
    private LocalDateTime passwordModifiedDate;
    public enum Gender {MALE, FEMALE}

    @ManyToMany(fetch = FetchType.EAGER, cascade = {
          //  CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REMOVE
    })
    @JoinTable(name = "users_to_group",
            joinColumns = @JoinColumn(name = "users_id"),
            inverseJoinColumns = @JoinColumn(name = "user_group_id")
    )
    private Set<GroupEntity> groups = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "website_id")
    private WebsiteEntity website;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        boolean isSuperAdmin = CmsSecurityUtils.hasGroup(this, CmsUtils.GROUP_SUPER_ADMIN);
        return isSuperAdmin ? CmsSecurityUtils.fullPrivilegeList : getGrantedAuthorities(getPrivileges(groups));
    }


    public Set<GrantedAuthority> getGrantedAuthorities(){
        Collection<GroupEntity> privileges = this.getGroups();
        return getGrantedAuthorities(getPrivileges(groups));
    }

    public Set<String> getRolesSet(){
        Collection<GroupEntity> privileges = this.getGroups();
        Set<GrantedAuthority> grantedAuthorities = getGrantedAuthorities(getPrivileges(groups));
        Set<String> roles = new HashSet<>();
        for (GrantedAuthority grantedAuthority : grantedAuthorities) {
            roles.add(grantedAuthority.getAuthority());
        }
        return roles;
    }

    public List<String> getPrivileges(final Collection<GroupEntity> roles) {
        final List<String> privileges = new ArrayList<>();
        final List<PermissionEntity> collection = new ArrayList<>();
        for (final GroupEntity role : roles) {
            collection.addAll(role.getPermissions());
        }
        for (final PermissionEntity item : collection) {
            privileges.add(item.getName());
        }
        return privileges;
    }

    private Set<GrantedAuthority> getGrantedAuthorities(final List<String> privileges) {
        final Set<GrantedAuthority> authorities = new HashSet<>();
        for (final String privilege : privileges) {
            authorities.add(new SimpleGrantedAuthority(privilege));
        }
        return authorities;
    }

    @Column(nullable = false, columnDefinition = "TINYINT(1) default '1'")
    private boolean enabled = true;
    @Column(nullable = false, columnDefinition = "TINYINT(1) default '1'")
    private boolean accountNonExpired = true;
    @Column(nullable = false, columnDefinition = "TINYINT(1) default '1'")
    private boolean accountNonLocked = true;
    @Column(nullable = false, columnDefinition = "TINYINT(1) default '1'")
    private boolean credentialsNonExpired = true;
    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public boolean hasGroup(String group){
        return this.getGroups().stream().anyMatch(g -> g.getName().equals(group));
    }
}
