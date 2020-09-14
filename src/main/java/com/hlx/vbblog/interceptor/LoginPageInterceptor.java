package com.hlx.vbblog.interceptor;


import com.hlx.vbblog.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class LoginPageInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = StringUtils.getIp(request);
        log.info("{}---请求登录页", ip);
        return true;
    }
}
