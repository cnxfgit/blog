package com.hlx.vbblog.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hlx.vbblog.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.info("登录成功");
        String userId = null;
        Object principal = authentication.getPrincipal();
        if (principal != null) {
            User user = (User) principal;
            request.getSession().setAttribute("user", user);
            userId = user.getId().toString();
        }
        String parameter = request.getParameter("remember-me");
        boolean rememberMe = Boolean.parseBoolean(parameter);
        if (rememberMe) {
            Cookie cookie = new Cookie("userId", userId);
            cookie.setMaxAge(14 * 24 * 60 * 60);
            response.addCookie(cookie);
        }
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpStatus.OK.value());
        response.getWriter().write(objectMapper.writeValueAsString(authentication));
    }
}
