package com.zxx.tinycat.core.http.util;

public class StringUtil {
    public static boolean beginWith(String s, String prefix) {
        if (prefix == null) {
            return true;
        }
        if (s == null) {
            return false;
        }
        if (s.length() < prefix.length()) {
            return false;
        }
        for (int i = 0; i < prefix.length(); i++) {
            if (s.charAt(i) != prefix.charAt(i)) {
                return false;
            }
        }
        return true;
    }

}
