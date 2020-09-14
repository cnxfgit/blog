package com.hlx.vbblog.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hlx.vbblog.model.AccessLog;
import com.hlx.vbblog.query.LogQuery;
import com.hlx.vbblog.vo.ViewDateVO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

public interface AccessLogService {
    /**
     * 保存日志数据
     *
     * @param username  用户
     * @param browser   浏览器
     * @param ip        请求IP
     * @param joinPoint /
     * @param log       日志实体
     */
    @Async
    void save(String username, String browser, String ip, ProceedingJoinPoint joinPoint, AccessLog log);

    /**
     * 分页获取所有日志
     *
     * @param current  当前页码
     * @param size     页码大小
     * @param logQuery
     * @return 日志列表
     */
    Page<AccessLog> listByPage(Integer current, Integer size, LogQuery logQuery);

    /**
     * 根据ID删除日志
     *
     * @param id 日志ID
     */
    void remove(Long id);

    /**
     * 根据ID列表批量删除日志
     *
     * @param idList 日志ID列表
     */
    void removeByIdList(List<Long> idList);

    /**
     * 统计访问总数
     *
     * @return 访问总数
     */
    Integer countAll();

    /**
     * 查询最近的访问日志
     *
     * @return 访问日志列表
     */
    List<AccessLog> listNewest();

    /**
     * 统计最近7天的访问数
     */
    List<ViewDateVO> countByLast7Days();

    /**
     * 统计上次访问首页至现在增加的浏览次数
     */
    Integer countByThirtyDays();
}
