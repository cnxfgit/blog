package com.hlx.vbblog.exception;

/**
 * 实体存在异常
 **/
public class EntityExistException extends RuntimeException {
    public EntityExistException(String message) {
        super(message);
    }

    public EntityExistException(String entity, String field, String val) {
        super(EntityExistException.generateMessage(entity, field, val));
    }

    private static String generateMessage(String entity, String field, String val) {
        return entity + "[" + field + "]: " + val + " 已存在";
    }
}
