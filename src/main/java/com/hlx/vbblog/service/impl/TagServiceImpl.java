package com.hlx.vbblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hlx.vbblog.common.TableConstant;
import com.hlx.vbblog.dao.TagMapper;
import com.hlx.vbblog.exception.EntityExistException;
import com.hlx.vbblog.model.Tag;
import com.hlx.vbblog.query.TagQuery;
import com.hlx.vbblog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "tag")
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;

    @Override
    @Cacheable
    public Page<Tag> listTableByPage(Integer current, Integer size, TagQuery tagQuery) {
        Page<Tag> tagPage = new Page<>(current, size);
        QueryWrapper<Tag> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(tagQuery.getName())) {
            wrapper.like(Tag.Table.NAME, tagQuery.getName());
        }
        if (tagQuery.getStartDate() != null && tagQuery.getEndDate() != null) {
            wrapper.between(TableConstant.TAG_ALIAS +Tag.Table.CREATE_TIME, tagQuery.getStartDate(), tagQuery.getEndDate());
        }
        return tagMapper.listTableByPage(tagPage, wrapper);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void saveOfUpdate(Tag tag) {
        QueryWrapper<Tag> wrapper = new QueryWrapper<>();
        wrapper.eq(Tag.Table.NAME, tag.getName());
        if (tag.getId() == null) {
            //新增
            //检查分类名称是否唯一
            if (tagMapper.selectOne(wrapper) != null) {
                throw new EntityExistException("标签名称已存在");
            }
            tagMapper.insert(tag);
        } else {
            //更新
            List<Tag> tags = tagMapper.selectList(wrapper);
            tags = tags.stream().filter(c -> !c.getId().equals(tag.getId())).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(tags)) {
                throw new EntityExistException("标签名称已存在");
            }
            tagMapper.updateById(tag);
        }
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Long id) {
        tagMapper.deleteById(id);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void removeByIdList(List<Long> idList) {
        tagMapper.deleteBatchIds(idList);
    }

    @Override
    @Cacheable
    public Tag getById(Long id) {
        return tagMapper.selectById(id);
    }

    @Override
    @Cacheable
    public List<Tag> listByArticleCount() {
        return tagMapper.listByArticleCount();
    }

    @Override
    @Cacheable
    public long countAll() {
        return tagMapper.selectCount(null);
    }

    @Override
    @Cacheable
    public List<String> listColor() {
        QueryWrapper<Tag> wrapper = new QueryWrapper<>();
        wrapper.select(Tag.Table.COLOR)
                .groupBy(Tag.Table.COLOR);
        List<Tag> tags = tagMapper.selectList(wrapper);
        return tags.stream().map(Tag::getColor).collect(Collectors.toList());
    }

    @Override
    @Cacheable(key = "'listByArticleId:'+#id")
    public List<Tag> listByArticleId(Long id) {
        return tagMapper.selectByArticleId(id);
    }

    @Override
    @Cacheable
    public List<Tag> listAll() {
        return tagMapper.selectList(null);
    }
}
