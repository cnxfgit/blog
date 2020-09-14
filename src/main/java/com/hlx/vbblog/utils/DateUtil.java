package com.hlx.vbblog.utils;

public class DateUtil {
    public static String formatDate(Integer year, Integer month, Integer day) {
        if (day != null) {
            return String.format("%4d-%02d-%02d", year, month, day);
        } else {
            return String.format("%4d-%02d", year, month);
        }
    }
}
