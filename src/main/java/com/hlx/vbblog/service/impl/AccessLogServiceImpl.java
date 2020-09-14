package com.hlx.vbblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hlx.vbblog.common.Constant;
import com.hlx.vbblog.common.TableConstant;
import com.hlx.vbblog.dao.AccessLogMapper;
import com.hlx.vbblog.model.AccessLog;
import com.hlx.vbblog.query.LogQuery;
import com.hlx.vbblog.service.AccessLogService;
import com.hlx.vbblog.utils.StringUtils;
import com.hlx.vbblog.utils.UserInfoUtil;
import com.hlx.vbblog.vo.ViewDateVO;
import org.apache.commons.lang3.time.DateUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

@Service
public class AccessLogServiceImpl implements AccessLogService {

    @Autowired
    private AccessLogMapper accessLogMapper;

    @Override
    public List<AccessLog> listNewest() {
        QueryWrapper<AccessLog> wrapper = new QueryWrapper<>();
        wrapper.select(AccessLog.Table.ID, AccessLog.Table.REQUEST_IP, AccessLog.Table.ADDRESS, AccessLog.Table.CREATE_TIME, AccessLog.Table.DESCRIPTION, AccessLog.Table.STATUS)
                .orderByDesc(AccessLog.Table.CREATE_TIME)
                .last(TableConstant.LIMIT + Constant.NEWEST_PAGE_SIZE);
        return accessLogMapper.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(String username, String browser, String ip, ProceedingJoinPoint joinPoint, AccessLog log) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        com.hlx.vbblog.anntation.AccessLog aopLog = method.getAnnotation(com.hlx.vbblog.anntation.AccessLog.class);

        // 方法路径
        String methodName = joinPoint.getTarget().getClass().getName() + "." + signature.getName() + "()";

        StringBuilder params = new StringBuilder("{");
        //参数值
        Object[] argValues = joinPoint.getArgs();
        //参数名称
        String[] argNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        if (argValues != null) {
            for (int i = 0; i < argValues.length; i++) {
                params.append(" ").append(argNames[i]).append(": ").append(argValues[i]);
            }
        }
        // 描述
        if (log != null) {
            log.setDescription(aopLog.value());
        }
        assert log != null;
        log.setRequestIp(ip);

        String loginPath = "login";
        if (loginPath.equals(signature.getName())) {
            try {
                assert argValues != null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.setAddress(StringUtils.getCityInfo(log.getRequestIp()));
        log.setMethod(methodName);
        log.setUsername(username);
        log.setParams(params.toString() + " }");
        log.setBrowser(browser);
        log.setCreateTime(new Date());
        accessLogMapper.insert(log);
    }

    @Override
    public Integer countAll() {
        return accessLogMapper.selectCount(null);
    }

    @Override
    public Integer countByThirtyDays() {
        String username = UserInfoUtil.getUsername();
        if (org.springframework.util.StringUtils.isEmpty(username)) {
            return 0;
        }
        QueryWrapper<AccessLog> wrapper = new QueryWrapper<>();
        wrapper.between(AccessLog.Table.CREATE_TIME, DateUtils.addDays(new Date(),-30), new Date());
        return accessLogMapper.selectCount(wrapper);
    }

    @Override
    public List<ViewDateVO> countByLast7Days() {
        return accessLogMapper.countByLast7Days();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeByIdList(List<Long> idList) {
        accessLogMapper.deleteBatchIds(idList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long id) {
        accessLogMapper.deleteById(id);
    }

    @Override
    public Page<AccessLog> listByPage(Integer current, Integer size, LogQuery logQuery) {
        Page<AccessLog> page = new Page<>(current, size);
        QueryWrapper<AccessLog> wrapper = new QueryWrapper<>();
        wrapper.select(AccessLog.Table.ID, AccessLog.Table.REQUEST_IP, AccessLog.Table.ADDRESS, AccessLog.Table.DESCRIPTION, AccessLog.Table.BROWSER, AccessLog.Table.TIME, AccessLog.Table.CREATE_TIME, AccessLog.Table.STATUS)
                .orderByDesc(AccessLog.Table.CREATE_TIME);
        if (!StringUtils.isEmpty(logQuery.getStatus())) {
            wrapper.eq(AccessLog.Table.STATUS, logQuery.getStatus());
        }
        if (logQuery.getStartDate() != null && logQuery.getEndDate() != null) {
            wrapper.between(AccessLog.Table.CREATE_TIME, logQuery.getStartDate(), logQuery.getEndDate());
        }
        return accessLogMapper.selectPage(page, wrapper);
    }
}
