package com.hlx.vbblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hlx.vbblog.dao.RoleMapper;
import com.hlx.vbblog.dao.RoleMenuMapper;
import com.hlx.vbblog.dao.RoleUserMapper;
import com.hlx.vbblog.dao.UserMapper;
import com.hlx.vbblog.exception.AssociationExistException;
import com.hlx.vbblog.exception.EntityExistException;
import com.hlx.vbblog.model.Role;
import com.hlx.vbblog.model.RoleMenu;
import com.hlx.vbblog.model.RoleUser;
import com.hlx.vbblog.query.RoleQuery;
import com.hlx.vbblog.service.RoleService;
import com.hlx.vbblog.utils.RedisUtils;
import com.hlx.vbblog.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "role")
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private RoleUserMapper roleUserMapper;
    @Autowired
    private RoleMenuMapper roleMenuMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisUtils redisUtils;

    @Override
    @Cacheable
    public Role getById(Long id) {
        Role role = new Role();
        //查询角色信息
        QueryWrapper<Role> roleWrapper = new QueryWrapper<>();
        roleWrapper.select(Role.Table.ID, Role.Table.ROLE_NAME, Role.Table.DESCRIPTION, Role.Table.RANK, Role.Table.COLOR).eq(Role.Table.ID, id);
        Role r = roleMapper.selectOne(roleWrapper);
        BeanUtils.copyProperties(r, role);
        //查询角色权限信息
        QueryWrapper<RoleMenu> roleMenuWrapper = new QueryWrapper<>();
        roleMenuWrapper.select(RoleMenu.Table.MENU_ID)
                .eq(RoleMenu.Table.ROLE_ID, id);
        List<RoleMenu> roleMenus = roleMenuMapper.selectList(roleMenuWrapper);
        List<Long> menuIdList = roleMenus.stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
        role.setMenuIdList(menuIdList);
        return role;
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Long id) {
        QueryWrapper<RoleUser> wrapper = new QueryWrapper<>();
        wrapper.eq(RoleUser.Table.ROLE_ID, id);
        //角色存在关联用户则不允许删除
        Integer count = roleUserMapper.selectCount(wrapper);
        if (count >= 1) {
            throw new AssociationExistException("该角色存在关联用户，不能删除");
        }
        roleMapper.deleteById(id);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void removeByIdList(List<Long> idList) {
        for (Long id : idList) {
            QueryWrapper<RoleUser> roleUserWrapper = new QueryWrapper<>();
            roleUserWrapper.eq(RoleUser.Table.ROLE_ID, id);
            //角色存在关联用户则不允许删除
            Integer count = roleUserMapper.selectCount(roleUserWrapper);
            if (count >= 1) {
                QueryWrapper<Role> roleWrapper = new QueryWrapper<>();
                roleWrapper.select(Role.Table.ROLE_NAME).eq(Role.Table.ID, id);
                Role role = roleMapper.selectOne(roleWrapper);
                throw new AssociationExistException("角色: " + role.getRoleName() + "存在关联用户，不能删除");
            }
        }
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void saveOfUpdate(Role role) {
        if (role.getId() == null) {
            //保存
            //检查角色名称是否唯一
            QueryWrapper<Role> wrapper = new QueryWrapper<>();
            wrapper.eq(Role.Table.ROLE_NAME, role.getRoleName());
            if (!CollectionUtils.isEmpty(roleMapper.selectList(wrapper))) {
                throw new EntityExistException("角色名称：" + role.getRoleName() + "已存在");
            }
            //保存角色
            roleMapper.insert(role);
            //保存角色权限
            roleMenuMapper.insertBatch(role.getId(), role.getMenuIdList());
        } else {
            //更新
            //检查角色名称是否唯一
            QueryWrapper<Role> roleWrapper = new QueryWrapper<>();
            roleWrapper.eq(Role.Table.ROLE_NAME, role.getRoleName());
            List<Role> roles = roleMapper.selectList(roleWrapper);
            roles = roles.stream().filter(r -> !r.getId().equals(role.getId())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(roles)) {
                throw new EntityExistException("角色名称：" + role.getRoleName() + "已存在");
            }
            //更新角色
            roleMapper.updateById(role);
            //更新角色权限
            //先删除原有角色权限
            QueryWrapper<RoleMenu> roleMenuWrapper = new QueryWrapper<>();
            roleMenuWrapper.eq(RoleMenu.Table.ROLE_ID, role.getId());
            roleMenuMapper.delete(roleMenuWrapper);
            //再添加新的角色权限
            roleMenuMapper.insertBatch(role.getId(), role.getMenuIdList());
        }
    }

    @Override
    @Cacheable
    public Page<Role> listTableByPage(Integer current, Integer size, RoleQuery roleQuery) {
        Page<Role> page = new Page<>(current, size);
        QueryWrapper<Role> roleWrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(roleQuery.getRoleName())) {
            roleWrapper.like(Role.Table.ROLE_NAME, roleQuery.getRoleName());
        }
        if (!StringUtils.isEmpty(roleQuery.getDescription())) {
            roleWrapper.like(Role.Table.DESCRIPTION, roleQuery.getDescription());
        }
        if (roleQuery.getStartDate() != null && roleQuery.getEndDate() != null) {
            roleWrapper.between(Role.Table.CREATE_TIME, roleQuery.getStartDate(), roleQuery.getEndDate());
        }
        //查询关联用户
        Page<Role> pageInfo = roleMapper.selectPage(page, roleWrapper);
        for (Role role : pageInfo.getRecords()) {
            QueryWrapper<RoleUser> roleUserWrapper = new QueryWrapper<>();
            roleUserWrapper.eq(RoleUser.Table.ROLE_ID, role.getId());
            Integer count = roleUserMapper.selectCount(roleUserWrapper);
            role.setUserCount(count);
        }
        return pageInfo;
    }

    @Override
    @Cacheable
    public List<Role> listAll() {
        QueryWrapper<Role> wrapper = new QueryWrapper<>();
        wrapper.select(Role.Table.ID, Role.Table.ROLE_NAME);
        return roleMapper.selectList(wrapper);
    }
}
