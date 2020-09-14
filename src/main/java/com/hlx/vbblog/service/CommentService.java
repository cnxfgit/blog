package com.hlx.vbblog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hlx.vbblog.model.Comment;
import com.hlx.vbblog.query.CommentQuery;

import java.util.List;

public interface CommentService {
    /**
     * 保存评论
     * @param comment 评论
     */
    void save(Comment comment);

    /**
     * 分页查询文章的所有评论
     * @return 评论分页
     */
    Page<Comment> listByArticleId(Long aid, Integer current, Integer size);

    /**
     * 后台分页查询所有评论
     *
     * @param current      当前页码
     * @param size         页面大小
     * @param commentQuery 查询条件
     * @return 评论列表
     */
    Page<Comment> listTableByPage(Integer current, Integer size, CommentQuery commentQuery);

    /**
     * 根据文章ID减少评论量
     *
     * @param id 文章ID
     */
    void decreaseArticleComments(Long id);

    /**
     * 根据ID列表批量删除评论
     *
     * @param idList 评论ID列表
     */
    void removeByIdList(List<Long> idList);

    /**
     * 根据ID删除评论
     *
     * @param id 评论ID
     */
    void removeById(Long id);

    /**
     * 根据ID回复评论
     *
     * @param comment 评论
     */
    void reply(Comment comment);

    /**
     * 统计评论总数
     *
     * @return 评论总数
     */
    Integer countAll();

    /**
     * 查询最近的评论
     *
     * @return 评论列表
     */
    List<Comment> listNewest();

    /**
     * 统计上次访问首页至现在增加的评论数
     */
    Integer countByThirtyDays();
}
