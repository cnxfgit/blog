package com.hlx.vbblog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hlx.vbblog.model.AccessLog;
import com.hlx.vbblog.vo.ViewDateVO;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessLogMapper extends BaseMapper<AccessLog> {
    /**
     * 统计最近7天的前台流量量
     */
    List<ViewDateVO> countByLast7Days();
}
