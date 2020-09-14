package com.hlx.vbblog.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hlx.vbblog.model.Tag;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 根据文章ID查询标签
     *
     * @param articleId 文章ID
     * @return 标签列表
     */
    List<Tag> selectByArticleId(@Param("articleId") long articleId);

    /**
     * 查询所有标签（统计文章数目）
     *
     * @return 标签列表
     */
    List<Tag> listByArticleCount();

    /**
     * 后台查询所有标签
     *
     * @param page         分页参数
     * @param queryWrapper 条件
     * @return 标签列表
     */
    Page<Tag> listTableByPage(IPage<Tag> page, @Param("ew") QueryWrapper<Tag> queryWrapper);
}
