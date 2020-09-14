package com.hlx.vbblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hlx.vbblog.common.TableConstant;
import com.hlx.vbblog.dao.RoleUserMapper;
import com.hlx.vbblog.dao.UserMapper;
import com.hlx.vbblog.exception.BadRequestException;
import com.hlx.vbblog.exception.EntityExistException;
import com.hlx.vbblog.model.RoleUser;
import com.hlx.vbblog.model.User;
import com.hlx.vbblog.query.UserQuery;
import com.hlx.vbblog.service.UserService;
import com.hlx.vbblog.utils.StringUtils;
import com.hlx.vbblog.vo.UserInfoVO;
import com.hlx.vbblog.vo.UserLoginVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "user")
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleUserMapper roleUserMapper;

    @Override
    @Cacheable
    public User getById(Long id) {
        QueryWrapper<User> userWrapper = new QueryWrapper<>();
        userWrapper.select(User.Table.ID, User.Table.USERNAME, User.Table.NICKNAME, User.Table.EMAIL, User.Table.STATUS)
                .eq(User.Table.ID, id);
        User user = userMapper.selectOne(userWrapper);
        QueryWrapper<RoleUser> roleUserWrapper = new QueryWrapper<>();
        roleUserWrapper.select(RoleUser.Table.ROLE_ID)
                .eq(RoleUser.Table.USER_ID, id);
        List<RoleUser> roleUsers = roleUserMapper.selectList(roleUserWrapper);
        if (!CollectionUtils.isEmpty(roleUsers)) {
            user.setRoleId(roleUsers.get(0).getRoleId());
        }
        return user;
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Long id) {
        userMapper.deleteById(id);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void removeByIdList(List<Long> idList) {
        userMapper.deleteBatchIds(idList);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(User user) {
        userMapper.updateById(user);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void saveOfUpdate(User user) {
        if (user.getId() == null) {
            //保存
            //验证用户名是否唯一
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.eq(User.Table.USERNAME, user.getUsername());
            if (null != userMapper.selectOne(wrapper)) {
                throw new EntityExistException("用户", "用户名", user.getUsername());
            }
            //验证邮箱是否唯一
            wrapper.clear();
            wrapper.eq(User.Table.EMAIL, user.getEmail());
            if (null != userMapper.selectOne(wrapper)) {
                throw new EntityExistException("用户", "邮箱", user.getEmail());
            }
            userMapper.insert(user);
            RoleUser roleUser = new RoleUser();
            roleUser.setRoleId(user.getRoleId());
            roleUser.setUserId(user.getId());
            roleUserMapper.insert(roleUser);
        } else {
            //首先更新用户
            userMapper.updateById(user);
            //再添加用户角色关联
            RoleUser roleUser = new RoleUser();
            roleUser.setUserId(user.getId());
            roleUser.setRoleId(user.getRoleId());
            QueryWrapper<RoleUser> roleUserWrapper = new QueryWrapper<>();
            roleUserWrapper.eq(RoleUser.Table.USER_ID, user.getId());
            if (!CollectionUtils.isEmpty(roleUserMapper.selectList(roleUserWrapper))) {
                //有记录则先删除
                roleUserMapper.delete(roleUserWrapper);
            }
            //最后添加
            roleUserMapper.insert(roleUser);
        }
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void updateInfo(UserInfoVO userInfoVO) {
        //验证手机号码是否唯一
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        List<User> users = userMapper.selectList(wrapper);
        users = users.stream().filter(u -> !u.getId().equals(userInfoVO.getId())).collect(Collectors.toList());
        //验证邮箱是否唯一
        wrapper.clear();
        users = userMapper.selectList(wrapper);
        users = users.stream().filter(u -> !u.getId().equals(userInfoVO.getId())).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(users)) {
            throw new EntityExistException("用户", "邮箱", userInfoVO.getEmail());
        }
        User user = new User();
        BeanUtils.copyProperties(userInfoVO, user);
        userMapper.updateById(user);
    }

    @Override
    @Cacheable
    public Integer countAll() {
        return userMapper.selectCount(null);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(UserLoginVO passwordVO) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.select(User.Table.PASSWORD).eq(User.Table.ID, passwordVO.getUserId());
        User user = userMapper.selectOne(wrapper);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(passwordVO.getOldPassword(), user.getPassword())) {
            throw new BadRequestException(HttpStatus.BAD_REQUEST, "旧密码输入不正确");
        }
        user.setId(passwordVO.getUserId());
        user.setPassword(encoder.encode(passwordVO.getNewPassword()));
        userMapper.updateById(user);
    }

    @Override
    @Cacheable
    public Page<User> listTableByPage(int current, int size, UserQuery userQuery) {
        Page<User> page = new Page<>(current, size);
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(userQuery.getUsername())) {
            wrapper.like(User.Table.USERNAME, userQuery.getUsername());
        }
        if (!StringUtils.isEmpty(userQuery.getEmail())) {
            wrapper.like(User.Table.EMAIL, userQuery.getEmail());
        }
        if (userQuery.getStartDate() != null && userQuery.getEndDate() != null) {
            wrapper.between(TableConstant.USER_ALIAS + User.Table.CREATE_TIME, userQuery.getStartDate(), userQuery.getEndDate());
        }
        return userMapper.listTableByPage(page, wrapper);
    }

    @Override
    @Cacheable
    public User checkUser(String username, String password) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.select(User.Table.ID, User.Table.USERNAME)
                .eq(User.Table.USERNAME, username)
                .eq(User.Table.PASSWORD, password);
        return userMapper.selectOne(wrapper);
    }
}
