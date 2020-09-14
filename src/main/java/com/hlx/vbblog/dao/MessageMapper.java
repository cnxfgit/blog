package com.hlx.vbblog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hlx.vbblog.model.Message;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageMapper extends BaseMapper<Message> {

    /**
     * 分页查询所有顶级留言
     *
     * @param page 分页参数
     * @return 留言分页
     */
    Page<Message> listRootByPage(IPage<Message> page);

    /**
     * 查询所有留言
     *
     * @return 留言列表
     */
    List<Message> listAll();
}
