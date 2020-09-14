package com.hlx.vbblog.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hlx.vbblog.model.Article;
import com.hlx.vbblog.vo.ArticleDateVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ArticleMapper extends BaseMapper<Article> {
    /**
     * 后台分页查询文章
     *
     * @param page         分页参数
     * @param queryWrapper 条件
     * @return 文章列表
     */
    Page<Article> listTableByPage(IPage<Article> page, @Param("ew") QueryWrapper<Article> queryWrapper);

    /**
     * 前台查询推荐文章
     *
     * @param limit 最大限制
     * @return 推荐文章列表
     */
    List<Article> listUpdate(@Param("limit") int limit);

    /**
     * 前台分页查询文章预览
     *
     * @param page 分页参数
     * @return 文章预览列表
     */
    Page<Article> listPreviewByPage(IPage<Article> page);

    /**
     * 前台根据ID查询文章详情
     *
     * @param id 文章ID
     * @return 文章详情
     */
    Article selectDetailById(Long id);

    /**
     * 前台获取当前文章的上一篇文章预览
     *
     * @param id 当前文章ID
     * @return 上一篇文章预览
     */
    Article selectPrevPreviewById(Long id);

    /**
     * 前台获取当前文章的下一篇文章预览
     *
     * @param id 当前文章ID
     * @return 下一篇文章预览
     */
    Article selectNextPreviewById(Long id);

    /**
     * 前台根据分类ID分页查询分类的文章预览
     *
     * @param page       分页参数
     * @param categoryId 分类ID
     * @return 文章预览分页
     */
    Page<Article> listPreviewPageByCategoryId(IPage<Article> page, @Param("categoryId") Long categoryId);

    /**
     * 前台根据标签ID分页查询标签的文章预览
     *
     * @param page  分页参数
     * @param tagId 标签ID
     * @return 文章预览分页
     */
    Page<Article> listPreviewPageByTagId(IPage<Article> page, @Param("tagId") Long tagId);

    /**
     * 前台根据日期统计文章数量
     *
     * @param dateFilterType 日期统计类型
     * @return 文章日期统计
     */
    List<ArticleDateVO> countByDate(@Param("dft") Integer dateFilterType);

    /**
     * 前台根据日期分页查询所有文章预览
     *
     * @param page 分页参数
     * @return 文章预览列表
     */
    Page<Article> listPreviewPageByDate(IPage<Article> page);
}
