package com.hlx.vbblog.utils;

import com.hlx.vbblog.common.Constant;
import com.hlx.vbblog.exception.BadRequestException;
import com.hlx.vbblog.model.User;

public class UserInfoUtil {
    public static String getUsername() {
        Object o = RequestHolder.getHttpServletRequest().getSession().getAttribute(Constant.USER);
        String username = "";
        if (o != null) {
            User user = (User) o;
            username = user.getUsername();
        } else {
            throw new BadRequestException("用户未登录");
        }
        return username;
    }

    public static Long getId() {
        Object o = RequestHolder.getHttpServletRequest().getSession().getAttribute(Constant.USER);
        Long id = null;
        if (o != null) {
            User user = (User) o;
            id = user.getId();
        } else {
            throw new BadRequestException("用户未登录");
        }
        return id;
    }
}
