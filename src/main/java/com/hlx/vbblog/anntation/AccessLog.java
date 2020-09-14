package com.hlx.vbblog.anntation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义访问日志注解
 **/
@Target(ElementType.METHOD)//作用于方法上
@Retention(RetentionPolicy.RUNTIME)//保留至运行时
public @interface AccessLog {
    String value() default "";
}
