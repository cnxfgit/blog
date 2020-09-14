package com.hlx.vbblog.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 自定义响应码
 **/

@Getter
@AllArgsConstructor
public enum ResultEnum {
    /**
     * 操作成功
     */
    SUCCESS(200, "操作成功"),
    /**
     * 操作失败
     */
    FAILURE(505, "操作失败"),
    /**
     * 找不到资源
     */
    NOT_FOUND(404, "找不到资源"),
    /**
     * 服务器错误
     */
    UNKNOWN_ERROR(500, "未知错误");
    private Integer code;

    private String message;

    public static ResultEnum getEnumByCode(Integer code) {
        for (ResultEnum e : ResultEnum.values()) {
            if (e.getCode().equals(code)) {
                return e;
            }
        }
        return null;
    }
}
