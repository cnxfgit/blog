package com.hlx.vbblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hlx.vbblog.common.Constant;
import com.hlx.vbblog.dao.LinkMapper;
import com.hlx.vbblog.exception.EntityExistException;
import com.hlx.vbblog.model.Link;
import com.hlx.vbblog.query.LinkQuery;
import com.hlx.vbblog.service.LinkService;
import com.hlx.vbblog.utils.StringUtils;
import com.hlx.vbblog.vo.AuditVO;
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
@CacheConfig(cacheNames = "link")
public class LinkServiceImpl implements LinkService {

    @Autowired
    private LinkMapper linkMapper;

    @Override
    @Cacheable
    public Page<Link> listTableByPage(Integer current, Integer size, LinkQuery linkQuery) {
        Page<Link> page = new Page<>(current, size);
        QueryWrapper<Link> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(linkQuery.getNickname())) {
            wrapper.like(Link.Table.NICKNAME, linkQuery.getNickname());
        }
        if (linkQuery.getStartDate() != null && linkQuery.getEndDate() != null) {
            wrapper.between(Link.Table.CREATE_TIME, linkQuery.getStartDate(), linkQuery.getEndDate());
        }
        if (linkQuery.getStatus() != null) {
            wrapper.eq(Link.Table.STATUS, linkQuery.getStatus());
        }
        return linkMapper.selectPage(page, wrapper);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void audit(AuditVO auditVO) {
        Link link = new Link();
        link.setId(auditVO.getId());
        link.setStatus(auditVO.getStatus());
        linkMapper.updateById(link);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void removeByIdList(List<Long> idList) {
        linkMapper.deleteBatchIds(idList);
    }

    @Override
    @Cacheable
    public Page<Link> listByPage(Integer current, Integer size) {
        Page<Link> page = new Page<>(current, size);
        QueryWrapper<Link> wrapper = new QueryWrapper<>();
        wrapper.select(Link.Table.ID, Link.Table.NICKNAME, Link.Table.AVATAR, Link.Table.INTRODUCTION, Link.Table.LINK)
                .eq(Link.Table.STATUS, Constant.AUDIT_PASS)
                .orderByAsc(Link.Table.SORT);
        return linkMapper.selectPage(page, wrapper);
    }

    @Override
    @Cacheable
    public Link getById(Long id) {
        return linkMapper.selectById(id);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void saveOfUpdate(Link link) {
        if (link.getId() == null) {
            //保存
            //验证昵称是否唯一
            QueryWrapper<Link> wrapper = new QueryWrapper<>();
            wrapper.eq(Link.Table.NICKNAME, link.getNickname());
            if (null != linkMapper.selectOne(wrapper)) {
                throw new EntityExistException("昵称：" + link.getNickname() + "已存在");
            }
            linkMapper.insert(link);
        } else {
            //更新
            //验证昵称是否唯一
            QueryWrapper<Link> wrapper = new QueryWrapper<>();
            wrapper.eq(Link.Table.NICKNAME, link.getNickname());
            List<Link> links = linkMapper.selectList(wrapper);
            links = links.stream().filter(u -> !u.getId().equals(link.getId())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(links)) {
                throw new EntityExistException("昵称:" + link.getNickname() + " 已存在");
            }
            linkMapper.updateById(link);
        }
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Long id) {
        linkMapper.deleteById(id);
    }
}
