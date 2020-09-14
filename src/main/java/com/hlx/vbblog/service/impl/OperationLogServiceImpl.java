package com.hlx.vbblog.service.impl;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hlx.vbblog.common.Constant;
import com.hlx.vbblog.common.TableConstant;
import com.hlx.vbblog.dao.OperationLogMapper;
import com.hlx.vbblog.model.AccessLog;
import com.hlx.vbblog.model.OperationLog;
import com.hlx.vbblog.query.LogQuery;
import com.hlx.vbblog.service.OperationLogService;
import com.hlx.vbblog.utils.StringUtils;
import com.hlx.vbblog.vo.ViewDateVO;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;

@Service
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    private OperationLogMapper operationLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(String username, String browser, String ip, ProceedingJoinPoint joinPoint, OperationLog log) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        com.hlx.vbblog.anntation.OperationLog aopLog = method.getAnnotation(com.hlx.vbblog.anntation.OperationLog.class);

        // 方法路径
        String methodName = joinPoint.getTarget().getClass().getName() + "." + signature.getName() + "()";

        StringBuilder params = new StringBuilder("{");
        //参数值
        Object[] argValues = joinPoint.getArgs();
        //参数名称
        String[] argNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        if (argValues != null) {
            for (int i = 0; i < argValues.length; i++) {
                if (argValues[i].toString().length() > 100) {
                    argValues[i] = "...";
                }
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
                username = new JSONObject(argValues[0]).get("username").toString();
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
        operationLogMapper.insert(log);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeByIdList(List<Long> idList) {
        operationLogMapper.deleteBatchIds(idList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remove(Long id) {
        operationLogMapper.deleteById(id);
    }


    @Override
    public List<ViewDateVO> countByLast7Days() {
        return operationLogMapper.countByLast7Days();
    }

    @Override
    public List<OperationLog> listNewest() {
        QueryWrapper<OperationLog> wrapper = new QueryWrapper<>();
        wrapper.select(OperationLog.Table.ID, OperationLog.Table.REQUEST_IP, OperationLog.Table.USERNAME, OperationLog.Table.ADDRESS, OperationLog.Table.CREATE_TIME, OperationLog.Table.DESCRIPTION, OperationLog.Table.STATUS)
                .orderByDesc(OperationLog.Table.CREATE_TIME)
                .last(TableConstant.LIMIT + Constant.NEWEST_PAGE_SIZE);
        return operationLogMapper.selectList(wrapper);
    }

    @Override
    public Page<OperationLog> listByPage(Integer current, Integer size, LogQuery logQuery) {
        Page<OperationLog> page = new Page<>(current, size);
        QueryWrapper<OperationLog> wrapper = new QueryWrapper<>();
        wrapper.select(OperationLog.Table.ID, OperationLog.Table.REQUEST_IP, OperationLog.Table.ADDRESS, OperationLog.Table.USERNAME, OperationLog.Table.DESCRIPTION, OperationLog.Table.BROWSER, OperationLog.Table.TIME, OperationLog.Table.CREATE_TIME, OperationLog.Table.STATUS)
                .orderByDesc(OperationLog.Table.CREATE_TIME);
        if (!StringUtils.isEmpty(logQuery.getStatus())) {
            wrapper.eq(AccessLog.Table.STATUS, logQuery.getStatus());
        }
        if (logQuery.getStartDate() != null && logQuery.getEndDate() != null) {
            wrapper.between(OperationLog.Table.CREATE_TIME, logQuery.getStartDate(), logQuery.getEndDate());
        }
        return operationLogMapper.selectPage(page, wrapper);
    }
}
