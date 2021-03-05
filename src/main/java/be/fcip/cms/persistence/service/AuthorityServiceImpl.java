package be.fcip.cms.persistence.service;

import be.fcip.cms.persistence.model.GroupEntity;
import be.fcip.cms.persistence.model.PermissionEntity;
import be.fcip.cms.persistence.repository.IGroupRepository;
import be.fcip.cms.persistence.repository.IPermissionRepository;
import be.fcip.cms.util.CmsUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import java.util.*;

@Service
@Transactional
public class AuthorityServiceImpl implements IAuthorityService {


    @Autowired
    private IGroupRepository groupRepository;

    @Autowired
    private IPermissionRepository roleRepository;

    @Override
    public GroupEntity findGroupById(Long id) {
        Optional<GroupEntity> byId = groupRepository.findById(id);

        return byId.orElse(null);
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
        return groupRepository.save(role);
    }

    @Override
    @Caching(evict = { @CacheEvict(value = "user", allEntries = true) })
    public List<GroupEntity> saveGroups(List<GroupEntity> roles) {
        return groupRepository.saveAll(roles);
    }

    @Override
    @Caching(evict = { @CacheEvict(value = "user", allEntries = true) })
    public PermissionEntity saveRole(PermissionEntity privilege) {
        return roleRepository.save(privilege);
    }

    @Override
    @Caching(evict = { @CacheEvict(value = "user", allEntries = true) })
    public List<PermissionEntity> saveRoles(List<PermissionEntity> privileges) {
        return roleRepository.saveAll(privileges);
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
