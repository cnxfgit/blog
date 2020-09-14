package com.hlx.vbblog.dao;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hlx.vbblog.model.Category;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryMapper extends BaseMapper<Category> {
    /**
     * 查询所有分类（统计文章数目）
     *
     * @return 分类列表
     */
    List<Category> listByArticleCount();

    /**
     * 后台查询所有分类
     *
     * @param page         分页参数
     * @param queryWrapper 条件
     * @return 分类列表
     */
    Page<Category> listTableByPage(IPage<Category> page, @Param("ew") QueryWrapper<Category> queryWrapper);
}
