package com.hlx.vbblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hlx.vbblog.common.Constant;
import com.hlx.vbblog.common.TableConstant;
import com.hlx.vbblog.dao.MessageMapper;
import com.hlx.vbblog.model.Message;
import com.hlx.vbblog.query.MessageQuery;
import com.hlx.vbblog.service.MessageService;
import com.hlx.vbblog.utils.LinkedListUtil;
import com.hlx.vbblog.utils.StringUtils;
import com.hlx.vbblog.utils.UserInfoUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@CacheConfig(cacheNames = "message")
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Override
    public Integer countByThirtyDays() {
        String username = UserInfoUtil.getUsername();
        if (org.springframework.util.StringUtils.isEmpty(username)) {
            return 0;
        }
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.between(Message.Table.CREATE_TIME, DateUtils.addDays(new Date(),-30), new Date());
        return messageMapper.selectCount(wrapper);
    }

    @Override
    @Cacheable
    public List<Message> listNewest() {
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.select(Message.Table.ID, Message.Table.NICKNAME, Message.Table.CONTENT, Message.Table.CREATE_TIME)
                .orderByDesc(Message.Table.CREATE_TIME)
                .last(TableConstant.LIMIT + Constant.NEWEST_PAGE_SIZE);
        return messageMapper.selectList(wrapper);
    }

    @Override
    @Cacheable
    public Page<Message> listTableByPage(Integer current, Integer size, MessageQuery messageQuery) {
        Page<Message> page = new Page<>(current, size);
        QueryWrapper<Message> wrapper = new QueryWrapper<>();
        wrapper.select(Message.Table.ID, Message.Table.NICKNAME, Message.Table.PID, Message.Table.CONTENT, Message.Table.CREATE_TIME, Message.Table.REQUEST_IP, Message.Table.ADDRESS);
        if (!StringUtils.isEmpty(messageQuery.getNickname())) {
            wrapper.like(Message.Table.NICKNAME, messageQuery.getNickname());
        }
        if (messageQuery.getStartDate() != null && messageQuery.getEndDate() != null) {
            wrapper.between(Message.Table.CREATE_TIME, messageQuery.getStartDate(), messageQuery.getEndDate());
        }
        wrapper.orderByDesc(Message.Table.CREATE_TIME);
        return messageMapper.selectPage(page, wrapper);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void removeById(Long id) {
        messageMapper.deleteById(id);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void removeByIdList(List<Long> idList) {
        messageMapper.deleteBatchIds(idList);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void reply(Message message) {
        messageMapper.insert(message);
    }

    @Override
    @Cacheable
    public Integer countAll() {
        return messageMapper.selectCount(null);
    }

    @Override
    @CacheEvict(allEntries = true)
    @Transactional(rollbackFor = Exception.class)
    public void save(Message message) {
        messageMapper.insert(message);
    }

    @Override
    @Cacheable
    public Page<Message> listByPage(Integer current, Integer size) {
        Page<Message> page = new Page<>(current, size);
        Page<Message> pageInfo = messageMapper.listRootByPage(page);
        List<Message> messages = messageMapper.listAll();
        LinkedListUtil.toMessageLinkedList(pageInfo.getRecords(), messages);
        return pageInfo;
    }
}
