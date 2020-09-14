package com.hlx.vbblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hlx.vbblog.common.Constant;
import com.hlx.vbblog.common.TableConstant;
import com.hlx.vbblog.dao.NoticeMapper;
import com.hlx.vbblog.model.Notice;
import com.hlx.vbblog.query.NoticeQuery;
import com.hlx.vbblog.service.NoticeService;
import com.hlx.vbblog.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@CacheConfig(cacheNames = "notice")
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    @Cacheable
    public Page<Notice> listTableByPage(Integer current, Integer size, NoticeQuery noticeQuery) {
        Page<Notice> page = new Page<>(current, size);
        QueryWrapper<Notice> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(noticeQuery.getTitle())) {
            wrapper.like(Notice.Table.TITLE, noticeQuery.getTitle());
        }
        if (!StringUtils.isEmpty(noticeQuery.getContent())) {
            wrapper.like(Notice.Table.CONTENT, noticeQuery.getContent());
        }
        if (noticeQuery.getStartDate() != null && noticeQuery.getEndDate() != null) {
            wrapper.between(Notice.Table.CREATE_TIME, noticeQuery.getStartDate(), noticeQuery.getEndDate());
        }
        if (noticeQuery.getDisplay() != null) {
            wrapper.eq(Notice.Table.DISPLAY, noticeQuery.getDisplay());
        }
        wrapper.orderByDesc(Notice.Table.CREATE_TIME);
        return noticeMapper.selectPage(page, wrapper);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Long id) {
        noticeMapper.deleteById(id);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void removeByIdList(List<Long> idList) {
        noticeMapper.deleteBatchIds(idList);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void saveOfUpdate(Notice notice) {
        if (notice.getId() == null) {
            noticeMapper.insert(notice);
        } else {
            noticeMapper.updateById(notice);
        }
    }

    @Override
    @Cacheable
    public Notice getById(Long id) {
        QueryWrapper<Notice> wrapper = new QueryWrapper<>();
        wrapper.select(Notice.Table.ID, Notice.Table.TITLE, Notice.Table.CONTENT, Notice.Table.DISPLAY)
                .eq(Notice.Table.ID, id);
        return noticeMapper.selectOne(wrapper);
    }

    @Override
    @Cacheable
    public List<Notice> listNewest() {
        QueryWrapper<Notice> wrapper = new QueryWrapper<>();
        wrapper.select(Notice.Table.ID, Notice.Table.TITLE, Notice.Table.CONTENT, Notice.Table.CREATE_TIME)
                .eq(Notice.Table.DISPLAY, Constant.DISPLAY)
                .orderByDesc(Notice.Table.CREATE_TIME)
                .last(TableConstant.LIMIT + Constant.NEWEST_PAGE_SIZE);
        return noticeMapper.selectList(wrapper);
    }
}
