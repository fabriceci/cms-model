package be.fcip.cms.persistence.service;

import be.fcip.cms.persistence.model.GroupEntity;
import be.fcip.cms.persistence.model.PermissionEntity;
import be.fcip.cms.persistence.model.UserEntity;
import be.fcip.cms.persistence.repository.IGroupRepository;
import be.fcip.cms.persistence.repository.IPermissionRepository;
import be.fcip.cms.persistence.repository.IUserRepository;
import be.fcip.cms.util.ApplicationUtils;
import be.fcip.cms.util.CmsUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AuthorityServiceImpl implements IAuthorityService {

    @Autowired private IUserRepository userRepository;
    @Autowired private IGroupRepository groupRepository;
    @Autowired private IPermissionRepository roleRepository;
    @Autowired private CacheManager cacheManager;

    @Override
    public GroupEntity findGroupById(Long id) {
        return groupRepository.findById(id).orElse(null);
    }

    @Override
    public GroupEntity findGroupByName(String name) {
        return groupRepository.findByName(name);
    }

    @Override
    public PermissionEntity findRoleById(Long id) {
        return roleRepository.findById(id).orElse(null);
    }

    @Override
    public PermissionEntity findRoleByName(String name) {
        return roleRepository.findByName(name);
    }

    @Override
    public List<GroupEntity> findAllGroup() {
        return groupRepository.findAll();
    }

    @Override
    public List<PermissionEntity> findAllRole() {
        return roleRepository.findAll();
    }

    @Override
    public List<GroupEntity> findAllClientGroup() {
        return groupRepository.findAllForClient(CmsUtils.GROUP_SUPER_ADMIN);
    }

    @Override
    @Caching(evict = { @CacheEvict(value = "user", allEntries = true) })
    public GroupEntity saveGroup(GroupEntity role) {
        List<UserEntity> users = userRepository.findAllByGroupsName(role.getName());
        Cache cache = cacheManager.getCache("user");
        for (UserEntity user : users) {
            cache.evict(user.getEmail());
        }
        return groupRepository.save(role);
    }

    @Override
    @Caching(evict = { @CacheEvict(value = "user", allEntries = true) })
    public PermissionEntity savePermission(PermissionEntity privilege) {
        PermissionEntity save = roleRepository.save(privilege);
        ApplicationUtils.refreshPermission(roleRepository);
        return save;
    }

    @Override
    public Map<String, List<PermissionEntity>> getRoleByGroup(){
        List<PermissionEntity> privileges = roleRepository.findAll();
        Map<String,List<PermissionEntity>> result = new HashMap<>();
        for (PermissionEntity privilege : privileges) {
            List<PermissionEntity> list = result.get(privilege.getSection());
            if(list == null){
                list = new ArrayList<>();
            }
            list.add(privilege);
            result.put(privilege.getSection(), list);
        }
        return result;
    }

    @Override
    public void deleteGroup(Long id) {
        GroupEntity group = groupRepository.findById(id).orElse(null);
        if(group == null){
            throw new IllegalArgumentException();
        }
        if(!group.isDeletable()){
            throw new AccessDeniedException("Undeletable group");
        }

        groupRepository.delete(group);

    }

    @Override
    public String jsonAdminGroup() {

        JsonArrayBuilder data = Json.createArrayBuilder();
        JsonObjectBuilder row;

        for (GroupEntity group : findAllClientGroup()) {
            row = Json.createObjectBuilder();
            row.add("DT_RowData", Json.createObjectBuilder().add("id", group.getId()));
            row.add("name", StringUtils.trimToEmpty(group.getName()));
            row.add("description", StringUtils.trimToEmpty(group.getDescription()));
            row.add("deletable", group.isDeletable());
            data.add(row);
        }

        return Json.createObjectBuilder().add("data", data).build().toString();
    }
}
