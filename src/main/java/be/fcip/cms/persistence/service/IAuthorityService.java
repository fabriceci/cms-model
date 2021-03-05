package be.fcip.cms.persistence.service;

import be.fcip.cms.persistence.model.GroupEntity;
import be.fcip.cms.persistence.model.PermissionEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.Map;

public interface IAuthorityService {

    GroupEntity findGroupByName(String name);
    GroupEntity findGroupById(Long id);
    PermissionEntity findRoleByName(String name);
    PermissionEntity findRoleById(Long id);

    List<GroupEntity> findAllGroup();
    List<PermissionEntity> findAllRole();

    @PreAuthorize("hasRole('ROLE_ADMIN_GROUP')")
    GroupEntity saveGroup(GroupEntity group);
    @PreAuthorize("hasRole('ROLE_ADMIN_GROUP')")
    List<GroupEntity> saveGroups(List<GroupEntity> groups);
    @PreAuthorize("hasRole('ROLE_ADMIN_GROUP')")
    PermissionEntity saveRole(PermissionEntity role);
    @PreAuthorize("hasRole('ROLE_ADMIN_GROUP')")
    List<PermissionEntity> saveRoles(List<PermissionEntity> roles);

    /* TODO: Rename FindAllNonSuperAdmin */
    List<GroupEntity> findAllClientGroup();

    String jsonAdminGroup();

    Map<String, List<PermissionEntity>> getRoleByGroup();

    @PreAuthorize("hasRole('ROLE_ADMIN_GROUP_DELETE')")
    void deleteGroup(Long id);

}
