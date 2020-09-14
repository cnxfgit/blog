package com.hlx.vbblog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hlx.vbblog.model.Message;
import com.hlx.vbblog.query.MessageQuery;

import java.util.List;

public interface MessageService {
    /**
     * 后台分页查询所有留言
     *
     * @param current      当前页码
     * @param size         页面大小
     * @param messageQuery 查询条件
     * @return 留言分页
     */
    Page<Message> listTableByPage(Integer current, Integer size, MessageQuery messageQuery);


    /**
     * 根据ID删除留言
     *
     * @param id 留言ID
     */
    void removeById(Long id);

    /**
     * 根据ID列表批量删除留言
     *
     * @param idList 留言ID列表
     */
    void removeByIdList(List<Long> idList);

    void reply(Message message);

    /**
     * 分页查询所有留言
     *
     * @param current 当前页码
     * @param size    页面大小
     * @return 留言分页
     */
    Page<Message> listByPage(Integer current, Integer size);

    /**
     * 保存留言
     *
     * @param message 留言
     */
    void save(Message message);

    /**
     * 统计留言总数
     *
     * @return 留言总数
     */
    Integer countAll();

    /**
     * 查询最近的留言
     *
     * @return 留言列表
     */
    List<Message> listNewest();

    /**
     * 统计上次访问首页至现在增加的留言数
     */
    Integer countByThirtyDays();
}
