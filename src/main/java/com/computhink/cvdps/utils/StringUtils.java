package com.computhink.cvdps.utils;

public class StringUtils {

    public static boolean isNullOrEmpty(String s) {
        if (s == null || s.isEmpty() || s.trim() == "")
            return true;
        if ("null".equalsIgnoreCase(s))
            return true;
        return false;
    }

    public static boolean isEmpty(String s) {
        if (s != null && s.trim().isEmpty())
            return true;

        return false;
    }

    public static boolean isNotNullOrEmpty(String s) {
        return !isNullOrEmpty(s);
    }
}
